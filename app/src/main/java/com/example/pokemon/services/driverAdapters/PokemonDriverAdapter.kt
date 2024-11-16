// Ubicación: com/example/pokemon/driverAdapters/PokemonDriverAdapter.kt

package com.example.pokemon.driverAdapters

import com.example.pokemon.services.endpoints.PokedexApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokemonDriverAdapter {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val api: PokedexApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Conversor de JSON a objetos
            .build()
            .create(PokedexApi::class.java)
    }
}
