package com.example.pokemon.services.roomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon_favorites WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT * FROM pokemon_favorites")
    suspend fun getAllPokemons(): List<PokemonEntity>

    @Query("DELETE FROM pokemon_favorites WHERE name = :name")
    suspend fun deletePokemonByName(name: String)
}