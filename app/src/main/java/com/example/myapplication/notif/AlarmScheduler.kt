package com.example.myapplication.notif

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.myapplication.R
import com.example.myapplication.data.ReminderData
import com.example.myapplication.reminder.ReminderDialog
import java.util.*

object AlarmScheduler {

    /**
     * Schedules all the alarms for [ReminderData].
     *
     * @param context      current application context
     * @param reminderData ReminderData to use for the alarm
     */
    fun scheduleAlarmsForReminder(context: Context, reminderData: ReminderData) {

        // get the AlarmManager reference
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule the alarms based on the days to administer the medicine
        val days = context.resources.getStringArray(R.array.days)
        if (reminderData.days != null) {
            for (index in reminderData.days!!.indices) {

                val day = reminderData.days!![index]
                if (day != null) {

                    // get the PendingIntent for the alarm
                    val alarmIntent = createPendingIntent(context, reminderData, day)

                    // schedule the alarm
                    val dayOfWeek = getDayOfWeek(days, day)
                    scheduleAlarm(reminderData, dayOfWeek, alarmIntent, alarmMgr)
                }
            }
        }
    }

    /**
     * Schedules a single alarm
     */
    private fun scheduleAlarm(reminderData: ReminderData, dayOfWeek: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {

        // Set up the time to schedule the alarm
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(Calendar.HOUR_OF_DAY, reminderData.hour)
        datetimeToAlarm.set(Calendar.MINUTE, reminderData.minute)
        datetimeToAlarm.set(Calendar.SECOND, 0)
        datetimeToAlarm.set(Calendar.MILLISECOND, 0)
        datetimeToAlarm.set(Calendar.DAY_OF_WEEK, dayOfWeek)

        // Compare the datetimeToAlarm to today
        val today = Calendar.getInstance(Locale.getDefault())
        if (shouldNotifyToday(dayOfWeek, today, datetimeToAlarm)) {

            // schedule for today
            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
            return
        }

        // schedule 1 week out from the day
        datetimeToAlarm.roll(Calendar.WEEK_OF_YEAR, 1)
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
    }

    /**
     * Creates a [PendingIntent] for the Alarm using the [ReminderData]
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     * @param day          String representation of the day
     */
    private fun createPendingIntent(context: Context, reminderData: ReminderData, day: String?): PendingIntent? {
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_administer_medication)
            type = "$day-${reminderData.name}-${reminderData.medicine}-${reminderData.gender.name}"
            putExtra(ReminderDialog.KEY_ID, reminderData.id)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Determines if the Alarm should be scheduled for today.
     *
     * @param dayOfWeek day of the week as an Int
     * @param today today's datetime
     * @param datetimeToAlarm Alarm's datetime
     */
    private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
        return dayOfWeek == today.get(Calendar.DAY_OF_WEEK) &&
                today.get(Calendar.HOUR_OF_DAY) <= datetimeToAlarm.get(Calendar.HOUR_OF_DAY) &&
                today.get(Calendar.MINUTE) <= datetimeToAlarm.get(Calendar.MINUTE)
    }

    /**
     * Updates a notification.
     * Note: this just calls [AlarmScheduler.scheduleAlarmsForReminder] since
     * alarms with exact matching pending intents will update if they are already set, otherwise
     * call [AlarmScheduler.removeAlarmsForReminder] if the medicine has been administered.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun updateAlarmsForReminder(context: Context, reminderData: ReminderData) {
        if (!reminderData.administered) {
            AlarmScheduler.scheduleAlarmsForReminder(context, reminderData)
        } else {
            AlarmScheduler.removeAlarmsForReminder(context, reminderData)
        }
    }

    /**
     * Removes the notification if it was previously scheduled.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun removeAlarmsForReminder(context: Context, reminderData: ReminderData) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.action = context.getString(R.string.action_notify_administer_medication)
        intent.putExtra(ReminderDialog.KEY_ID, reminderData.id)

        // type must be unique so Intent.filterEquals passes the check to make distinct PendingIntents
        // Schedule the alarms based on the days to administer the medicine
        if (reminderData.days != null) {
            for (i in reminderData.days!!.indices) {
                val day = reminderData.days!![i]

                if (day != null) {
                    val type = String.format(Locale.getDefault(), "%s-%s-%s-%s", day, reminderData.name, reminderData.medicine, reminderData.gender.name)

                    intent.type = type
                    val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmMgr.cancel(alarmIntent)
                }
            }
        }
    }

    /**
     * Returns the int representation for the day of the week.
     *
     * @param days      array from resources
     * @param dayOfWeek String representation of the day e.g "Sunday"
     * @return [Calendar.DAY_OF_WEEK] for given dayOfWeek
     */
    private fun getDayOfWeek(days: Array<String>, dayOfWeek: String): Int {
        return when {
            dayOfWeek.equals(days[0], ignoreCase = true) -> Calendar.SUNDAY
            dayOfWeek.equals(days[1], ignoreCase = true) -> Calendar.MONDAY
            dayOfWeek.equals(days[2], ignoreCase = true) -> Calendar.TUESDAY
            dayOfWeek.equals(days[3], ignoreCase = true) -> Calendar.WEDNESDAY
            dayOfWeek.equals(days[4], ignoreCase = true) -> Calendar.THURSDAY
            dayOfWeek.equals(days[5], ignoreCase = true) -> Calendar.FRIDAY
            else -> Calendar.SATURDAY
        }
    }

}