package com.example.pokemon.services.endpoints

import com.example.pokemon.services.models.PokemonDetail
import com.example.pokemon.services.models.PokemonListResponse
import com.example.pokemon.services.models.PokemonResponse
import com.example.pokemon.services.models.PokemonTypeDetailResponse
import com.example.pokemon.services.models.PokemonTypeListResponse
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

    @GET("pokedex/original-johto/")
    suspend fun getPokedexJohto(): Response<PokemonResponse>

    @GET("pokedex/original-sinnoh/")
    suspend fun getPokedexSinnoh(): Response<PokemonResponse>

    @GET("pokedex/original-unova/")
    suspend fun getPokedexUnova(): Response<PokemonResponse>

    @GET("pokedex/kalos-central/")
    suspend fun getPokedexkalos(): Response<PokemonResponse>

    @GET("pokedex/original-alola/")
    suspend fun getPokedexAlola(): Response<PokemonResponse>

    @GET("pokemon/{pokemonName}/")
    suspend fun getPokemonDetails(@Path("pokemonName") pokemonName: String): Response<PokemonDetail>

    @GET("pokemon?limit=1500")
    suspend fun getPokemonList(): Response<PokemonListResponse>

    @GET("type/")
    suspend fun getPokemonTypes(): Response<PokemonTypeListResponse>

    @GET("type/{typeName}/")
    suspend fun getPokemonByType(@Path("typeName") typeName: String): Response<PokemonTypeDetailResponse>

}

data class RegionResponse(val results: List<Region>)
data class Region(val name: String, val url: String)
