package com.example.pokemon.services.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokemon.services.models.PokemonSprites

@Entity(tableName = "pokemon_favorites")
data class PokemonEntity(
    @PrimaryKey val name: String,
    val height: Int,
    val weight: Int,
    val types: String,
    val abilities: String,
    val moves: String
)