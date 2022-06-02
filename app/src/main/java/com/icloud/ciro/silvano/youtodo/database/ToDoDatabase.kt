package com.icloud.ciro.silvano.youtodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/* Classe ToDoDatabase
*  contiene i database holder e serve come principale punto di accesso
*  per sottolineare la connessione dell'app con i dati contenuti nel database
*/

@Database(entities = [Card::class, Category::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "toDo_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}