package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.PokemonDetail
import com.example.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonTheme {
                PokedexApp()
            }
        }
    }
}

@Composable
fun PokedexApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { PokedexScreen(navController) }
        composable("region/{regionName}") { backStackEntry ->
            val regionName = backStackEntry.arguments?.getString("regionName")
            regionName?.let { ShowRegionPokemonScreen(regionName = it, navController = navController) }
        }
        composable("pokemonDetail/{pokemonName}") { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
            PokemonDetailScreen(pokemonName = pokemonName, navController = navController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/Kanto")
                }
            ) {
                Text("Ver Pokémon de Kanto")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/Hoenn")
                }
            ) {
                Text("Ver Pokémon de Hoenn")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/Galar")
                }
            ) {
                Text("Ver Pokémon de Galar")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/Paldea")
                }
            ) {
                Text("Ver Pokémon de Paldea")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/Hisui")
                }
            ) {
                Text("Ver Pokémon de Hisui")
            }
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate("home") }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Pokedex",
            fontSize = 24.sp,
        )
    }
}

@Composable
fun PokemonDetailScreen(pokemonName: String, navController: NavController) {
    var pokemonDetails by remember { mutableStateOf<PokemonDetail?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pokemonName) {
        coroutineScope.launch {
            try {
                val response = PokemonDriverAdapter.api.getPokemonDetails(pokemonName)
                if (response.isSuccessful) {
                    pokemonDetails = response.body()
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
            pokemonDetails?.let { details ->
                // Nombre del Pokémon
                Text(
                    text = details.name.capitalize(),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                // Altura y peso
                Text("Height: ${details.height} decimeters")
                Text("Weight: ${details.weight} hectograms")

                // Tipos
                Text("Types: ${details.types.joinToString(", ") { type -> type.type.name.capitalize() }}")

                // Habilidades
                Text("Abilities: ${details.abilities.joinToString(", ") { ability -> ability.ability.name.capitalize() }}")

                // Categoría
                Text("Category: ${details.species.category.name.capitalize()}")

                // Descripción (Flavor Text)
                val description = details.species.flavor_text_entries.firstOrNull { it.language.name == "en" }?.flavor_text
                Text("Description: ${description ?: "No description available"}")

                // Movimientos
                Text("Moves:")
                details.moves.take(10).forEach { move ->
                    Text("- ${move.move.name.capitalize()}")
                }
            } ?: run {
                Text("Loading Pokémon details...", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
