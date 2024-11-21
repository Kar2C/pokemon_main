package com.example.pokemon.services.endpoints

import com.example.pokemon.services.models.PokemonResponse
import com.example.pokemon.services.models.RegionResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokedexApi {
    @GET("region/")
    suspend fun getRegions(): RegionResponse

    // Métodos específicos para cada región
    @GET("pokedex/kanto/")
    suspend fun getPokedexKanto(): Response<PokemonResponse>

    @GET("pokedex/hoenn/")
    suspend fun getPokedexHoenn(): Response<PokemonResponse>

    @GET("pokedex/galar/")
    suspend fun getPokedexGalar(): Response<PokemonResponse>

    @GET("pokedex/paldea/")
    suspend fun getPokedexPaldea(): Response<PokemonResponse>

    @GET("pokedex/hisui/")
    suspend fun getPokedexHisui(): Response<PokemonResponse>

}

data class RegionResponse(val results: List<Region>)

data class Region(val name: String, val url: String)
