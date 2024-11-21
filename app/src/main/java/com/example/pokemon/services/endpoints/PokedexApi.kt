package com.example.pokemon.services.endpoints

import com.example.pokemon.services.models.PokemonDetails
import com.example.pokemon.services.models.PokemonResponse
import com.example.pokemon.services.models.RegionResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokedexApi {
    @GET("region/")
    suspend fun getRegions(): RegionResponse

    @GET("pokedex/kanto/")
    suspend fun getPokedex(): Response<PokemonResponse>
}

data class RegionResponse(val results: List<Region>)

data class Region(val name: String, val url: String)
