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
import androidx.navigation.NavController
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.PokemonEntry
import kotlinx.coroutines.launch

@Composable
fun ShowRegionPokemonScreen(regionName: String, navController: NavController) {
    var pokemonList by remember { mutableStateOf<List<PokemonEntry>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(regionName) {
        coroutineScope.launch {
            try {
                when (regionName) {
                    "Kanto" -> {
                        val response = PokemonDriverAdapter.api.getPokedexKanto()
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                    "Hoenn" -> {
                        val response = PokemonDriverAdapter.api.getPokedexHoenn()
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                    "Galar" -> {
                        val response = PokemonDriverAdapter.api.getPokedexGalar()
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                    "Paldea" -> {
                        val response = PokemonDriverAdapter.api.getPokedexPaldea()
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                    "Hisui" -> {
                        val response = PokemonDriverAdapter.api.getPokedexHisui()
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                            // Navegar a la pantalla de detalles del Pokémon
                            navController.navigate("pokemonDetail/${pokemon.pokemon_species.name}")
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ) {
                        Text(text = pokemon.pokemon_species.name)
                    }
                }
            }

        }
    }
}
