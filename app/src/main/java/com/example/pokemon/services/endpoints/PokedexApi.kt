package com.example.pokemon.services.endpoints

import com.example.pokemon.services.models.PokemonDetails
import com.example.pokemon.services.models.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokedexApi {
    @GET("pokedex/")
    suspend fun getPokedex(): Response<PokemonResponse>

    @GET("pokemon/{id}/")  // Añadir esta función para obtener detalles de cada Pokémon
    suspend fun getPokemonDetails(@Path("id") id: Int): Response<PokemonDetails>
}
