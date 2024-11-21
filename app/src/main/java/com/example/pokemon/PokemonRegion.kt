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

    // Ejecutar la llamada a la API al cargar la pantalla
    LaunchedEffect(regionName) {
        coroutineScope.launch {
            try {
                when (regionName) {
                    "Kanto" -> {
                        val response = PokemonDriverAdapter.api.getPokedexKanto() // Llamada a Kanto
                        if (response.isSuccessful) {
                            pokemonList = response.body()?.pokemon_entries ?: emptyList()
                        }
                    }
                    "Hoenn" -> {
                        val response = PokemonDriverAdapter.api.getPokedexHoenn() // Llamada a Hoenn
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
        topBar = { TopBar(navController) }, // Añadir TopBar con ícono de Home
        bottomBar = { BottomNavigationBar() }
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
}
