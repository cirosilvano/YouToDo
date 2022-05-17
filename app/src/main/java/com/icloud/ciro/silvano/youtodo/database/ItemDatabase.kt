package com.icloud.ciro.silvano.youtodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*Classe ItemDatabase
* contiene i database holder e serve come principale punto di accesso
* per sottolineare la connessione dell'app con i dati contenuti nel database*/


@Database(entities = [Item::class, Category::class], version = 1, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getDatabase(context: Context): ItemDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                //l'istanza esiste già dunque viene ritornata
                return tempInstance
            }

            //creazione di una nuova istanza nel caso in cui tempIstance==null
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "user_database"
                ).build()
                //singleton
                INSTANCE = instance
                return instance
            }
        }
    }
}