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

data class RegionResponse(
    val results: List<Region>
)

data class Region(
    val name: String,
    val url: String
)

data class PokemonDetail(
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeEntry>
)

data class PokemonTypeEntry(
    val type: PokemonType
)

data class PokemonType(
    val name: String
)
