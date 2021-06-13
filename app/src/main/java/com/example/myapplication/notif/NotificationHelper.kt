package com.example.myapplication.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.data.ReminderData
import com.example.myapplication.reminder.ReminderDialog
import com.example.myapplication.ui.AppGlobalReceiver
import com.example.myapplication.ui.ReminderActivity

object NotificationHelper {

    private const val ADMINISTER_REQUEST_CODE = 2019

    /**
     * Sets up the notification channels for API 26+.
     * Note: This uses package name + channel name to create unique channelId's.
     *
     * @param context     application context
     * @param importance  importance level for the notificaiton channel
     * @param showBadge   whether the channel should have a notification badge
     * @param name        name for the notification channel
     * @param description description for the notification channel
     */
    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Helps issue the default application channels (package name + app name) notifications.
     * Note: this shows the use of [NotificationCompat.BigTextStyle] for expanded notifications.
     *
     * @param context    current application context
     * @param title      title for the notification
     * @param message    content text for the notification when it's not expanded
     * @param bigText    long form text for the expanded notification
     * @param autoCancel `true` or `false` for auto cancelling a notification.
     * if this is true, a [PendingIntent] is attached to the notification to
     * open the application.
     */
    fun createSampleDataNotification(context: Context, title: String, message: String,
                                     bigText: String, autoCancel: Boolean) {

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_stat_medicine)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(autoCancel)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(autoCancel)

            val intent = Intent(context, ReminderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }

    /**
     * Creates a notification for [ReminderData] with a full notification tap and a single action.
     *
     * @param context      current application context
     * @param reminderData ReminderData for this notification
     */

    fun createNotificationForPet(context: Context, reminderData: ReminderData) {

        // create a group notification
        val groupBuilder = buildGroupNotification(context, reminderData)

        // create the pet notification
        val notificationBuilder = buildNotificationForPet(context, reminderData)

        // add an action to the pet notification
        val administerPendingIntent = createPendingIntentForAction(context, reminderData)
        notificationBuilder.addAction(R.drawable.baseline_done_black_24, context.getString(R.string.administer), administerPendingIntent)

        // call notify for both the group and the pet notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(reminderData.gender.ordinal, groupBuilder.build())
        notificationManager.notify(reminderData.id, notificationBuilder.build())
    }

    /**
     * Builds and returns the [NotificationCompat.Builder] for the group notification for a pet type.
     *
     * @param context current application context
     * @param reminderData ReminderData for this notification
     */
    private fun buildGroupNotification(context: Context, reminderData: ReminderData): NotificationCompat.Builder {
        val channelId = "${context.packageName}-${reminderData.gender.name}"
        return NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_stat_medicine)
            setContentTitle(reminderData.gender.name)
            setContentText(context.getString(R.string.group_notification_for, reminderData.gender.name))
            setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.group_notification_for, reminderData.gender.name)))
            setAutoCancel(true)
            setGroupSummary(true)
            setGroup(reminderData.gender.name)
        }
    }

    /**
     * Builds and returns the NotificationCompat.Builder for the Pet notification.
     *
     * @param context current application context
     * @param reminderData ReminderData for this notification
     */
    private fun buildNotificationForPet(context: Context, reminderData: ReminderData): NotificationCompat.Builder {


        val channelId = "${context.packageName}-${reminderData.gender.name}"

        return NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_stat_medicine)
            setContentTitle(reminderData.name)
            setAutoCancel(true)

            // get a drawable reference for the LargeIcon
            val drawable = when (reminderData.gender) {
                ReminderData.GenderType.Man -> R.drawable.hombre
                ReminderData.GenderType.Woman -> R.drawable.mujer
//        else -> R.drawable.other
            }
            setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable))
            setContentText("${reminderData.medicine}, ${reminderData.desc}")
            setGroup(reminderData.gender.name)

            // note is not important so if it doesn't exist no big deal
            if (reminderData.note != null) {
                setStyle(NotificationCompat.BigTextStyle().bigText(reminderData.note))
            }

            // Launches the app to open the reminder edit screen when tapping the whole notification
            val intent = Intent(context, ReminderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(ReminderDialog.KEY_ID, reminderData.id)
            }

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }
    }

    /**
     * Creates the pending intent for the Administered Action for the pet notification.
     *
     * @param context current application context
     * @param reminderData ReminderData for this notification
     */
    private fun createPendingIntentForAction(context: Context, reminderData: ReminderData): PendingIntent? {
        /*
            Create an Intent to update the ReminderData if Administer action is clicked
         */
        val administerIntent = Intent(context, AppGlobalReceiver::class.java).apply {
            action = context.getString(R.string.action_medicine_administered)
            putExtra(AppGlobalReceiver.NOTIFICATION_ID, reminderData.id)
            putExtra(ReminderDialog.KEY_ID, reminderData.id)
            putExtra(ReminderDialog.KEY_ADMINISTERED, true)
        }

        return PendingIntent.getBroadcast(context, ADMINISTER_REQUEST_CODE, administerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}