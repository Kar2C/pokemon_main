package com.example.pokemon.services.models

import com.example.pokemon.services.roomDatabase.PokemonEntity

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
    val moves: List<PokemonMoveEntry>, // Movimientos (ataques)
    val sprites: PokemonSprites // Añadimos la propiedad para los sprites
)

fun PokemonEntity.toPokemonDetail(): PokemonDetail {
    return PokemonDetail(
        name = this.name,
        height = this.height,
        weight = this.weight,
        types = this.types.split(", ").map { typeName ->
            PokemonTypeEntry(
                type = PokemonType(
                    name = typeName,
                    url = "https://pokeapi.co/api/v2/type/$typeName" // URL simulada
                )
            )
        },
        abilities = this.abilities.split(", ").map { abilityName ->
            PokemonAbilityEntry(
                ability = Ability(
                    name = abilityName,
                    url = "https://pokeapi.co/api/v2/ability/$abilityName" // URL simulada
                )
            )
        },
        species = PokemonSpeciesDetail(
            name = this.name,
            url = "https://pokeapi.co/api/v2/pokemon-species/$name", // URL simulada
            flavor_text_entries = listOf(
                FlavorTextEntry(
                    flavor_text = "Descripción por defecto de $name.",
                    language = Language(name = "en")
                )
            ),
            category = Category(
                name = "Unknown Category" // Categoría por defect
            )
        ),
        moves = this.moves.split(", ").map { moveName ->
            PokemonMoveEntry(
                move = PokemonMove(
                    name = moveName
                )
            )
        },
        sprites = PokemonSprites(
            front_default = "https://pokeapi.co/media/sprites/pokemon/$name.png" // URL simulada
        )
    )
}

data class PokemonSprites(
    val front_default: String? // URL de la imagen frontal por defecto
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
    val url: String,
    var imageUrl: String? = null // Campo para la URL de la imagen
)

data class PokemonTypeListResponse(
    val results: List<PokemonType>
)

data class PokemonTypeDetailResponse(
    val pokemon: List<PokemonEntryDetail>
)

data class PokemonEntryDetail(
    val pokemon: PokemonBasicInfo
)

data class PokemonBasicInfo(
    val name: String,
    val url: String
)
