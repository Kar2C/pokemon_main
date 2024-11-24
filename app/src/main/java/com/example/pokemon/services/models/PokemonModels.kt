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
    val types: List<PokemonTypeEntry>, // Tipos del Pokémon
    val abilities: List<PokemonAbilityEntry>, // Habilidades
    val species: PokemonSpeciesDetail, // Información sobre la especie
    val moves: List<PokemonMoveEntry> // Movimientos (ataques)
)

data class PokemonTypeEntry(
    val type: PokemonType
)

data class PokemonType(
    val name: String,
    val url: String
)

data class PokemonAbilityEntry(
    val ability: Ability
)

data class Ability(
    val name: String,
    val url: String
)

data class PokemonSpeciesDetail(
    val name: String,
    val url: String,
    val flavor_text_entries: List<FlavorTextEntry>, // Descripción del Pokémon
    val category: Category // Categoría del Pokémon
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: Language
)

data class Language(
    val name: String
)

data class Category(
    val name: String
)

data class PokemonMoveEntry(
    val move: PokemonMove
)

data class PokemonMove(
    val name: String
)

data class PokemonListResponse(
    val results: List<Pokemon>
)

data class Pokemon(
    val name: String,
    val url: String
)

data class PokemonTypeListResponse(
    val results: List<PokemonType>
)