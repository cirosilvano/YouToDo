package com.icloud.ciro.silvano.youtodo

import android.util.Log

class DateTimeFormatHelper {
    companion object {
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
    }
}