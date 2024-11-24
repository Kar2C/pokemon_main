package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
            regionName?.let {
                ShowRegionPokemonScreen(
                    regionName = it,
                    navController = navController
                )
            }
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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/original-johto")
                }
            ) {
                Text("Ver Pokémon de Johto")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/original-sinnoh")
                }
            ) {
                Text("Ver Pokémon de Sinnoh")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/original-unova")
                }
            ) {
                Text("Ver Pokémon de Unova")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/kalos-central")
                }
            ) {
                Text("Ver Pokémon de Kalos")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    navController.navigate("region/original-alola")
                }
            ) {
                Text("Ver Pokémon de Alola")
            }
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    var expanded by remember { mutableStateOf(false) } // Controlar la visibilidad del menú
    var selectedOption by remember { mutableStateOf("Regiones") } // Opción seleccionada

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de Home
        IconButton(onClick = { navController.navigate("home") }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // Título "Pokedex"
        Text(
            text = "Pokedex",
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Botón para mostrar el menú desplegable
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu"
            )
        }

        // Menú desplegable con las opciones
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val options = listOf("Regiones", "Pokémon", "Tipos")
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption = option
                    expanded = false
                }) {
                    Text(text = option)  // Asegúrate de que se pase el texto correctamente
                }
            }
        }
    }

    // Mostrar contenido según la opción seleccionada
    when (selectedOption) {
        "Regiones" -> {
            // Navegar a la pantalla que muestra las regiones
            navController.navigate("home")
        }
        "Pokémon" -> {
            // Mostrar un mensaje de "Está en proceso"
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Está en proceso", fontSize = 20.sp)
            }
        }
        "Tipos" -> {
            // Mostrar un mensaje de "Está en proceso"
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Está en proceso", fontSize = 20.sp)
            }
        }
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

                // Descripción (Flavor Text)
                Text("Description: Aca va la descripcionnn")

                // Movimientos
                Text("Moves:")
                details.moves.take(10).forEach { move ->
                    Text("- ${move.move.name.capitalize()}")
                }
            } ?: run {
                Text(
                    "Loading Pokémon details...",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
