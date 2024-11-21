package com.example.pokemon

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.PokemonEntry
import kotlinx.coroutines.launch

@Composable
fun ShowRegionPokemonScreen(regionName: String) {
    var pokemonList by remember { mutableStateOf<List<PokemonEntry>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Ejecutar la llamada a la API al cargar la pantalla
    LaunchedEffect(regionName) {
        coroutineScope.launch {
            try {
                // Obtener el id de la región a partir del nombre (esto depende de cómo se maneja el endpoint de la API)
                val response = PokemonDriverAdapter.api.getPokedex() // Aquí puedes realizar la llamada adecuada para obtener los Pokémon de "Kanto"
                if (response.isSuccessful) {
                    pokemonList = response.body()?.pokemon_entries ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Pokémon de $regionName",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(pokemonList.size) { index ->
                val pokemon = pokemonList[index]
                Button(
                    onClick = {
                        // Aquí puedes navegar a la página de detalles del Pokémon
                        println("Clicked on Pokémon: ${pokemon.pokemon_species.name}")
                    },
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                ) {
                    Text(text = pokemon.pokemon_species.name)
                }
            }
        }
    }
}
