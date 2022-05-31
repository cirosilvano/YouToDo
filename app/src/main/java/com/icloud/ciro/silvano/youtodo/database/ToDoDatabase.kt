package com.icloud.ciro.silvano.youtodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/* Classe ItemDatabase
*  contiene i database holder e serve come principale punto di accesso
*  per sottolineare la connessione dell'app con i dati contenuti nel database
*/

@Database(entities = [Card::class, Category::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                //l'istanza esiste già, dunque viene ritornata
                return tempInstance
            }

            //creazione di una nuova istanza nel caso in cui tempIstance==null
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "user_database"
                ).allowMainThreadQueries().build()

                INSTANCE = instance
                return instance
            }
        }
    }
}