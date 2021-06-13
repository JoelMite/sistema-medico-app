package com.example.myapplication.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ReminderData(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("gender")
    var gender: GenderType = GenderType.Man,
    @SerializedName("medicine")
    var medicine: String? = null,
    @SerializedName("note")
    var note: String? = null,
    @SerializedName("desc")
    var desc: String? = null,
    @SerializedName("hour")
    var hour: Int = 0,
    @SerializedName("minute")
    var minute: Int = 0,
    @SerializedName("days")
    var days: Array<String?>? = null,
    @SerializedName("administered")
    var administered: Boolean = false
) : Parcelable {

    enum class GenderType {
        //        Other,
        Woman,
        Man
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeInt(id)
            writeString(name)
            writeString(gender.name)
            writeString(medicine)
            writeString(note)
            writeString(desc)
            writeInt(hour)
            writeInt(minute)
            writeStringArray(days)
            writeInt(if (administered) 1 else 0)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReminderData

        if (id != other.id) return false
        if (name != other.name) return false
        if (gender != other.gender) return false
        if (medicine != other.medicine) return false
        if (note != other.note) return false
        if (desc != other.desc) return false
        if (hour != other.hour) return false
        if (minute != other.minute) return false
        if (days != null) {
            if (other.days == null) return false
            if (!(days as Array).contentEquals(other.days as Array<out String>)) return false
        } else if (other.days != null) return false
        if (administered != other.administered) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + gender.hashCode()
        result = 31 * result + (medicine?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (desc?.hashCode() ?: 0)
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + (days?.contentHashCode() ?: 0)
        result = 31 * result + administered.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<ReminderData> {
        override fun createFromParcel(source: Parcel): ReminderData {
            return ReminderData().apply {
                id = source.readInt()
                name = source.readString()
                gender = ReminderData.GenderType.valueOf(source.readString()!!)
                medicine = source.readString()
                note = source.readString()
                desc = source.readString()
                hour = source.readInt()
                minute = source.readInt()
                source.readStringArray(days)
                administered = source.readInt() == 1
            }
        }

        override fun newArray(size: Int): Array<ReminderData?> {
            return arrayOfNulls(size)
        }
    }
}