package com.icloud.ciro.silvano.youtodo

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDateTime
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDeadlineString
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.weekDays
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/**
 * Testa tutti i metodi del companion object della classe DateTimeFormatHelper.
 * I test sono a grana grossa, ciascuno valida un metodo.
 *
 */
class DateTimeFormatHelperTest {

    val context : Context = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * Test corto perché il metodo è un semplice concatenatore di stringhe
     */
    @Test
    fun testGenerateDate() {
        assertEquals("18-07-2000", generateDate(2000, 7, 18, true))
        assertEquals("2000-07-18", generateDate(2000, 7, 18, false))
    }

    /**
     * Test corto perché il metodo è un semplice concatenatore di stringhe
     */
    @Test
    fun testGenerateTime() {
        assertEquals("18:07", generateTime(18, 7))
    }

    /**
     * Test corto perché il metodo è un semplice concatenatore di stringhe
     */
    @Test
    fun testGenerateDateTime() {
        assertEquals("29-12-1971, 19:48", generateDateTime(1971, 12, 29, 19, 48))
    }

    /**
     * Testa la generazione dinamica di deadline in base alla differenza tra il giorno corrente
     * e il giorno "puntato".
     *
     * Nota importante: nelle sezioni test today, tomorrow, yesterday e weekdays,
     * viene usato il metodo .dropLast(4) per eliminare gli ultimi 4 caratteri da
     * LocalDateTime.toString().
     * Il motivo è che LocalDateTime.toString() restituisce una stringa
     * nel formato YYYY-MM-DDTHH-MM-SS.mmm con .mmm i millisecondi,
     * mentre può fare il parsing solo di stringhe nel formato YYYY-MM-DDTHH:MM:SS
     * motivo per cui bisogna eliminare i millisecondi, che sono gli ultimi 4 char.
     */
    @Test
    fun testGenerateDeadlineString() {

        val res = context.resources

        // string values
        val yesterday = LocalDateTime.now().minusDays(1)
        val today = LocalDateTime.now()
        val tomorrow = LocalDateTime.now().plusDays(1)


        // test today
        assertEquals(
            "${res.getString(R.string.today)}, ${today.hour}:${today.minute}",
            generateDeadlineString(today.toString().dropLast(4), context)
        )

        // test yesterday
        assertEquals(
            "${res.getString(R.string.yesterday)}, ${yesterday.hour}:${yesterday.minute}",
            generateDeadlineString(yesterday.toString().dropLast(4), context)
        )

        // test tomorrow
        assertEquals("${res.getString(R.string.tomorrow)}, ${tomorrow.hour}:${tomorrow.minute}",
            generateDeadlineString(tomorrow.toString().dropLast(4), context))

        // test weekdays
        var dayString = ""
        var tdt: LocalDateTime

        for(i in 2..6) {
            tdt = today.plusDays(i.toLong())
            dayString = weekDays(tdt.dayOfWeek, res)
            assertEquals(
                "$dayString, ${tdt.hour}:${tdt.minute}",
                generateDeadlineString(tdt.toString().dropLast(4), context)
            )
        }

        // test date normali
        var tdtStr: String

        tdt = yesterday.minusDays(1) // l'altroieri
        tdtStr = generateDateTime(tdt.year, tdt.monthValue, tdt.dayOfMonth, tdt.hour, tdt.minute)
        assertEquals(
            tdtStr,
            generateDeadlineString(
                tdt.toString().dropLast(4),
                context
            )
        )

        tdt = today.plusDays(7) // più di 6 giorni
        tdtStr = generateDateTime(tdt.year, tdt.monthValue, tdt.dayOfMonth, tdt.hour, tdt.minute)
        assertEquals(
            tdtStr,
            generateDeadlineString(
                tdt.toString().dropLast(4),
                context
            )
        )


    }

}