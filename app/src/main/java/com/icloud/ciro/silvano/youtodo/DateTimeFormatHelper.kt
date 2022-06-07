package com.icloud.ciro.silvano.youtodo

import android.content.Context
import android.content.res.Resources
import java.lang.IllegalArgumentException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

class DateTimeFormatHelper {
    companion object {

        /**
         * Riceve in input un parametro DayOfWeek, convertendolo nella stringa localizzata appartenente
         * alle risorse indirizzate da res. Ad esempio, il DayOfWeek "THURSDAY" viene cercato in R.string.thursday
         * @param day - DayOfWeek, da convertire in stringa
         * @param res - Resources, risorse da cui estrarre la stringa
         * @return la stringa localizzata appartenente alle risorse indirizzate da res
         */
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

        /**
         * Genera una stringa Date-parseable (con parametro backwards = false) a partire da anno, mese e giorno.
         * Il formato restituito è YYYY-MM-DD per backwards=false, DD-MM-YYYY per backwards=true.
         * Il metodo non controlla la validità della data.
         *
         * @param year - Int, l'anno della data da convertire.
         * @param month - mese nel formato 1-12 dove 1 è gennaio, 12 dicembre.
         * @param day - giorno del mese (a partire da 1)
         * @param backwards - specifica il formato della data. Il valore false restituisce il formato YYYY-MM-DD,
         * mentre true restituisce DD-MM-YYYY. La data backwards non è Date-parseable
         * @return una stringa Date-parseable (con parametro backwards = false) a partire da anno, mese e giorno.
         */
        fun generateDate(year:Int, month: Int, day: Int, backwards: Boolean): String {
            var monthStr = (month).toString()
            var dayStr = day.toString()
            if(monthStr.length == 1) monthStr = "0${monthStr}"
            if(dayStr.length == 1) dayStr = "0${dayStr}"
            if(backwards) return "$dayStr-$monthStr-$year"
            return "$year-$monthStr-$dayStr"
        }

        /**
         * Genera una stringa contenente l'orario nel formato HH:MM
         *
         * @param hour - Int: ora da inserire nella stringa
         * @param minute - Int: minuto da inserire nella stringa
         * @return una stringa contenente l'orario nel formato HH:MM
         */
        fun generateTime(hour:Int, minute:Int): String{
            var hourString = hour.toString()
            var minuteString = minute.toString()
            if(hourString.length == 1) hourString = "0${hourString}"
            if(minuteString.length == 1) minuteString = "0${minuteString}"
            return "$hourString:$minuteString"
        }

        /**
         * Genera una stringa contenente un timestamp in formato YYYY-MM-DD, HH:MM a partire dagli argomenti di tipo Int.
         *
         * @param year - Int: l'anno da inserire nella stringa
         * @param month - Int: il mese da inserire nella stringa
         * @param day - Int: il giorno da inserire nella stringa
         * @param hour - Int: l'ora da inserire nella stringa
         * @param minute - Int: il minuto da inserire nella stringa
         * @return una stringa contenente un timestamp in formato "YYYY-MM-DD, HH:MM"
         */
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

        /**
         * Genera una stringa deadline di formato variabile in base alla distanza dell'istante in input e l'istante attuale.
         * Se il giorno è oggi, viene restituito "Oggi, HH:MM". Il medesimo meccanismo si applica se l'istante in input
         * appartiene al giorno di ieri, o domani. Se il giorno dell'istante in input ha una distanza (in giorni) inferiore
         * a 7 (una settimana), l'output sarà GDS, HH:MM, con GDS il giorno della settimana (ad esempio, Mercoledì, 18:30).
         * Se nessuno dei casi precedenti si applica, viene generata una data a partire dal metodo generateDateTime(Int,Int)
         *
         * @param deadline_tx - String: la stringa DateTime-parseable nel formato YYYY-MM-DDTHH:MM, con T carattere separatore
         * obbligatorio.
         * @param context- Context: il context da cui si risale alle Resources di localizzazione per ottenere le stringhe dei giorni della
         * settimana, e dei termini Oggi, Ieri, Domani.
         * @return una stringa deadline di formato variabile in base alla distanza dell'istante in input e l'istante attuale.
         * Se il giorno è oggi, viene restituito "Oggi, HH:MM". Il medesimo meccanismo si applica se l'istante in input
         * appartiene al giorno di ieri, o domani. Se il giorno dell'istante in input ha una distanza (in giorni) inferiore
         * a 7 (una settimana), l'output sarà GDS, HH:MM, con GDS il giorno della settimana (ad esempio, Mercoledì, 18:30).
         * Se nessuno dei casi precedenti si applica, viene generata una data a partire dal metodo generateDateTime(Int,Int)
         */
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