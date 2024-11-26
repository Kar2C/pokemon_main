package com.example.pokemon.services.roomDatabase

import android.content.Context
import androidx.room.Room

object DatabaseClient {

    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pokemon_database"
            ).build()
        }
        return INSTANCE!!
    }
}