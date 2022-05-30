package com.icloud.ciro.silvano.youtodo

import android.content.Context
import android.content.res.Resources
import android.util.Log
import java.lang.IllegalArgumentException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

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

        fun generateDeadlineString(deadline_tx: String, context: Context): String {
            // argument check
            if(deadline_tx.length != 19) throw IllegalArgumentException("Date string length not standard")

            // support variables
            val ldt = LocalDateTime.parse(deadline_tx)
            val ld = ldt.toLocalDate()
            val ldToday = LocalDate.now()
            val period = Period.between(ldToday, ld)
            val res = context.resources

            when(period.days) {
                -1 -> return "${res.getString(R.string.yesterday)}, ${generateTime(ldt.hour,ldt.minute)}"
                0 -> return "${res.getString(R.string.today)}, ${generateTime(ldt.hour,ldt.minute)}"
                1 -> return "${res.getString(R.string.tomorrow)}, ${generateTime(ldt.hour,ldt.minute)}"
            }

            if(ld.isAfter(ldToday) && period.days < 7) return "${weekDays(ld.dayOfWeek, res)}, ${generateTime(ldt.hour,ldt.minute)}"
            return generateDateTime(ldt.year,ldt.monthValue,ldt.dayOfMonth,ldt.hour, ldt.minute)
        }


    }
}