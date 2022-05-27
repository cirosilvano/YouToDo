package com.icloud.ciro.silvano.youtodo

import android.content.res.Resources
import android.util.Log
import java.time.DayOfWeek

class DateTimeFormatHelper {
    companion object {

        fun weekDays(day: DayOfWeek, res: Resources): String {
            when(day.toString()) {
                "MONDAY" -> return res.getString(R.string.monday)
                "TUESDAY" -> return res.getString(R.string.tuesday)
                "WEDNESDAY" -> return res.getString(R.string.wednesday)
                "THURSDAY" -> return res.getString(R.string.thursday)
                "FRIDAY" -> return res.getString(R.string.friday)
                "SATURDAY" -> return res.getString(R.string.saturday)
                "SUNDAY" -> return res.getString(R.string.sunday)
            }
            return "ERROR"
        }

        fun generateDate(year:Int, month: Int, day: Int, backwards: Boolean): String {
            var monthStr = (month).toString()
            var dayStr = day.toString()
            if(monthStr.length == 1) monthStr = "0${monthStr}"
            if(dayStr.length == 1) dayStr = "0${dayStr}"
            Log.d("", "genero data $dayStr/$monthStr/$year")
            if(backwards) return "$dayStr-$monthStr-$year"
            return "$year-$monthStr-$dayStr"
        }

        fun generateTime(hour:Int, minute:Int): String{
            var hourString = hour.toString()
            var minuteString = minute.toString()
            if(hourString.length == 1) hourString = "0${hourString}"
            if(minuteString.length == 1) minuteString = "0${minuteString}"
            return "$hourString:$minuteString"
        }

        fun generateDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): String {
            return "${
                generateDate(
                    year,
                    month,
                    day,
                    true
                )
            }, ${generateTime(hour, minute)}"
        }
    }
}