package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.Pokemon
import com.example.pokemon.services.models.PokemonDetail
import com.example.pokemon.services.models.PokemonType
import com.example.pokemon.services.roomDatabase.DatabaseClient
import com.example.pokemon.services.roomDatabase.PokemonEntity
import com.example.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    var selectedTabIndex by remember { mutableStateOf(0) } // Controlador de las pestañas
    var pokemonTypes by remember { mutableStateOf<List<PokemonType>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Realizar la llamada a la API para obtener los tipos de Pokémon
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = PokemonDriverAdapter.api.getPokemonTypes()
                if (response.isSuccessful) {
                    pokemonTypes = response.body()?.results ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column {
        // Barra de pestañas con los botones "Regiones", "Pokemones", "Tipos"
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                Text("Regiones", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                Text("Pokemones", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                Text("Tipos", modifier = Modifier.padding(16.dp))
            }
        }

        // Definimos la navegación
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                if (selectedTabIndex == 0) {
                    PokedexScreen(navController)  // Pantalla de regiones
                } else if (selectedTabIndex == 1) {
                    PokemonListScreen(navController)  // Pantalla de Pokémon
                } else if (selectedTabIndex == 2) {
                    ShowPokemonTypes(pokemonTypes, navController) // Mostrar los tipos
                }
            }

            // Nueva ruta para la pantalla de Pokémon por tipo
            composable("pokemonByType/{typeName}") { backStackEntry ->
                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""
                PokemonByTypeScreen(typeName = typeName, navController = navController)
            }

            composable("pokemonDetail/{pokemonName}") { backStackEntry ->
                val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
                PokemonDetailScreen(pokemonName = pokemonName, navController = navController)
            }

            composable("region/{regionName}") { backStackEntry ->
                val regionName = backStackEntry.arguments?.getString("regionName")
                regionName?.let {
                    ShowRegionPokemonScreen(regionName = it, navController = navController)
                }
            }
        }
    }
}


@Composable
fun ShowPokemonTypes(pokemonTypes: List<PokemonType>, navController: NavController) {
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
                text = "Tipos de Pokémon",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Mostrar los tipos de Pokémon
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(pokemonTypes.size) { index ->
                    val pokemonType = pokemonTypes[index]
                    Button(
                        onClick = {
                            // Navegar a la pantalla de Pokémon por tipo
                            navController.navigate("pokemonByType/${pokemonType.name}")
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ) {
                        Text(text = pokemonType.name.capitalize())
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonByTypeScreen(typeName: String, navController: NavController) {
    var pokemonList by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Realizar la llamada a la API para obtener los Pokémon de este tipo
    LaunchedEffect(typeName) {
        coroutineScope.launch {
            try {
                val response = PokemonDriverAdapter.api.getPokemonByType(typeName)
                if (response.isSuccessful) {
                    // Extraemos los nombres de los Pokémon de la respuesta
                    pokemonList = response.body()?.pokemon?.map { it.pokemon.name } ?: emptyList()
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
                text = "Pokémon de tipo ${typeName.capitalize()}",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Mostrar los Pokémon del tipo en botones
            if (pokemonList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(pokemonList.size) { index ->
                        val pokemonName = pokemonList[index]
                        Button(
                            onClick = {
                                // Navegar a la pantalla de detalles del Pokémon
                                navController.navigate("pokemonDetail/${pokemonName}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(text = pokemonName.capitalize())
                        }
                    }
                }
            } else {
                Text("Cargando Pokémon...", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun PokemonListScreen(navController: NavController) {
    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") } // Estado para la búsqueda
    val coroutineScope = rememberCoroutineScope()

    // Realizar la llamada a la API para obtener los Pokémon
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Llamamos al API para obtener la lista de los 1500 Pokémon
                val response = PokemonDriverAdapter.api.getPokemonList()
                if (response.isSuccessful) {
                    pokemonList = response.body()?.results ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Filtrar los Pokémon por la primera letra del nombre
    val filteredPokemonList = pokemonList.filter { pokemon ->
        pokemon.name.startsWith(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopBar(navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Barra de búsqueda en la parte superior
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it }, // Actualizar el estado con la búsqueda
                label = { Text("Buscar Pokémon por letra") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true
            )

            // Mostrar la lista filtrada de Pokémon
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filteredPokemonList.size) { index ->
                    val pokemon = filteredPokemonList[index]
                    Button(
                        onClick = {
                            // Navegar a la pantalla de detalles del Pokémon
                            navController.navigate("pokemonDetail/${pokemon.name}")
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ) {
                        Text(text = pokemon.name.capitalize())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            // Agregar botón Like en la parte inferior
            BottomBarLikeButton()
        },
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
    var isFavorite by remember { mutableStateOf(false) }  // Estado para verificar si es favorito
    val coroutineScope = rememberCoroutineScope()
    val pokemonDao = DatabaseClient.getInstance(LocalContext.current).pokemonDao()

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
        bottomBar = {
            // Botón "Like" en la parte inferior de todas las pantallas
            BottomBarLikeButton()
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            LaunchedEffect(pokemonDetails?.name) {
                pokemonDetails?.name?.let { name ->
                    val existingPokemon = pokemonDao.getPokemonByName(name)
                    isFavorite = existingPokemon != null
                }
            }


            pokemonDetails?.let { details ->
                Text(text = details.name.capitalize(), fontSize = 24.sp)
                Text("Height: ${details.height} decimeters")
                Text("Weight: ${details.weight} hectograms")
                Text("Types: ${details.types.joinToString(", ") { it.type.name.capitalize() }}")
                Text("Abilities: ${details.abilities.joinToString(", ") { it.ability.name.capitalize() }}")
                Text("Moves: ${details.moves.take(10).joinToString(", ") { it.move.name.capitalize() }}")

                // Botón para agregar a favoritos
                Button(
                    onClick = {
                        isFavorite = !isFavorite

                        coroutineScope.launch {
                            if(isFavorite){
                                val pokemonEntity = PokemonEntity(
                                    name = details.name,
                                    height = details.height,
                                    weight = details.weight,
                                    types = details.types.joinToString(", ") { it.type.name },
                                    abilities = details.abilities.joinToString(", ") { it.ability.name },
                                    moves = details.moves.take(10).joinToString(", ") { it.move.name }
                                )

                                pokemonDao.insertPokemon(pokemonEntity);
                                println("${details.name} ${ "Agregado a favoritos"}")
                            }else{
                                pokemonDao.deletePokemonByName(details.name)
                                println("${details.name} ${ "Eliminado de favoritos"}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(text = if (isFavorite) "Eliminar de Favoritos" else "Agregar a Favoritos")
                }
            } ?: run {
                Text("Loading Pokémon details...")
            }
        }
    }
}

@Composable
fun BottomBarLikeButton() {

    val pokemonDao = DatabaseClient.getInstance(LocalContext.current).pokemonDao()

    // Estado para almacenar la lista de Pokémon obtenida de la base de datos
    var pokemonList by remember { mutableStateOf<List<PokemonEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Función para obtener los Pokémon de la DB
    fun loadPokemonList() {
        // Usamos el lanzador de corutinas para hacer la consulta en un hilo de fondo
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            // Realizamos la consulta (simula obtener todos los Pokémon)
            val allPokemons = pokemonDao.getAllPokemons() // Esta consulta debe estar implementada en tu DAO
            // Regresamos al hilo principal para actualizar el estado
            pokemonList = allPokemons
            isLoading = false
        }
    }

    Button(
        onClick = {
            println("Like button clicked")
            loadPokemonList() // Cargar los Pokémon al hacer click

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "ver favorites")
    }

    if (isLoading) {
        Text("Cargando...")
    } else if (pokemonList.isEmpty()) {
        Text("No tienes favoritos.")
    } else {
        // Mostrar los Pokémon obtenidos
        Column(modifier = Modifier.fillMaxWidth()) {
            pokemonList.forEach { pokemon ->
                Text(text = pokemon.name, modifier = Modifier.padding(8.dp))
            }
        }
    }
}


