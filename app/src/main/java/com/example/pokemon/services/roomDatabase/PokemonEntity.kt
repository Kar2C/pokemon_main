package com.example.pokemon.services.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_favorites")
data class PokemonEntity(
    @PrimaryKey val name: String, // Utilizamos el nombre como identificador único
    val height: Int,
    val weight: Int,
    val types: String, // Guardamos los tipos como una cadena separada por comas
    val abilities: String, // Lo mismo con las habilidades
    val moves: String // También los movimientos como una cadena
)