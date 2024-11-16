package com.example.pokemon.services.models

data class PokemonResponse(
    val id: Int,
    val name: String,
    val pokemon_entries: List<PokemonEntry>
)

data class PokemonEntry(
    val entry_number: Int,
    val pokemon_species: PokemonSpecies
)

data class PokemonSpecies(
    val name: String,
    val url: String
)

data class PokemonDetails(
    val sprites: PokemonSprites
)

data class PokemonSprites(
    val front_default: String,   // Imagen normal
    val front_shiny: String     // Imagen shiny
)