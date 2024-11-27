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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.Pokemon
import com.example.pokemon.services.models.PokemonDetail
import com.example.pokemon.services.models.PokemonType
import com.example.pokemon.services.models.toPokemonDetail
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
    var selectedTabIndex by remember { mutableStateOf(0) }
    var pokemonTypes by remember { mutableStateOf<List<PokemonType>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

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

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                if (selectedTabIndex == 0) {
                    PokedexScreen(navController)
                } else if (selectedTabIndex == 1) {
                    PokemonListScreen(navController)
                } else if (selectedTabIndex == 2) {
                    ShowPokemonTypes(pokemonTypes, navController)
                }
            }

            composable("pokemonByType/{typeName}") { backStackEntry ->
                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""
                PokemonByTypeScreen(typeName = typeName, navController = navController)
            }

            composable("pokemonDetail/{pokemonName}") { backStackEntry ->
                val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
                PokemonDetailScreen(pokemonName = pokemonName, navController = navController)
            }

            composable("favorites") {
                FavoritesScreen(navController)
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(pokemonTypes.size) { index ->
                    val pokemonType = pokemonTypes[index]
                    Button(
                        onClick = {
                            navController.navigate("pokemonByType/${pokemonType.name}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
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

    LaunchedEffect(typeName) {
        coroutineScope.launch {
            try {
                val response = PokemonDriverAdapter.api.getPokemonByType(typeName)
                if (response.isSuccessful) {
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

            if (pokemonList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(pokemonList.size) { index ->
                        val pokemonName = pokemonList[index]
                        Button(
                            onClick = {
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
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = PokemonDriverAdapter.api.getPokemonList()
                if (response.isSuccessful) {
                    pokemonList = response.body()?.results ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Pokémon por letra") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filteredPokemonList.size) { index ->
                    val pokemon = filteredPokemonList[index]
                    Button(
                        onClick = {
                            navController.navigate("pokemonDetail/${pokemon.name}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Regiones",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 28.sp
                )
            )
            val buttonTextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/Kanto")
                    },
                ) {
                    Text("Kanto", style = buttonTextStyle)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/Hoenn")
                    },
                ) {
                    Text("Hoenn", style = buttonTextStyle)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/Galar")
                    },
                ) {
                    Text("Galar", style = buttonTextStyle)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/Paldea")
                    },
                ) {
                    Text("Paldea", style = buttonTextStyle)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/Hisui")
                    },
                ) {
                    Text("Hisui", style = buttonTextStyle)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/original-johto")
                    },
                ) {
                    Text("Johto", style = buttonTextStyle)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/original-sinnoh")
                    },
                ) {
                    Text("Sinnoh", style = buttonTextStyle)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/original-unova")
                    },
                ) {
                    Text("Unova", style = buttonTextStyle)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/kalos-central")
                    },
                ) {
                    Text("Kalos", style = buttonTextStyle)
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("region/original-alola")
                    },
                ) {
                    Text("Alola", style = buttonTextStyle)
                }
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
        IconButton(
            onClick = {
                navController.navigate("favorites")
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favoritos",
                modifier = Modifier.padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Pokedex",
            fontSize = 28.sp,
        )

    }
}

@Composable
fun PokemonDetailScreen(pokemonName: String, navController: NavController) {
    var pokemonDetails by remember { mutableStateOf<PokemonDetail?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var pokemonImageUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val pokemonDao = DatabaseClient.getInstance(LocalContext.current).pokemonDao()

    LaunchedEffect(pokemonName) {
        coroutineScope.launch {
            try {

                val response = PokemonDriverAdapter.api.getPokemonDetails(pokemonName)
                    if (response.isSuccessful) {
                        pokemonDetails = response.body()
                        pokemonDetails?.sprites?.front_default?.let {
                            pokemonImageUrl = it
                        }
                    }else{
                        val existingPokemon: PokemonEntity? = pokemonDao.getPokemonByName(pokemonName)
                        if(existingPokemon != null){
                            pokemonDetails = existingPokemon.toPokemonDetail()
                        }else{
                            println("Pokémon no encontrado en la DB")
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                val existingPokemon: PokemonEntity? = pokemonDao.getPokemonByName(pokemonName)
                if(existingPokemon != null){
                    pokemonDetails = existingPokemon.toPokemonDetail()
                }else{
                    println("Pokémon no encontrado en la DB")
                }

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
            LaunchedEffect(pokemonDetails?.name) {
                pokemonDetails?.name?.let { name ->
                    val existingPokemon = pokemonDao.getPokemonByName(name)
                    isFavorite = existingPokemon != null
                }
            }

            pokemonDetails?.let { details ->
                pokemonImageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen de ${details.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(text = details.name.capitalize(), fontSize = 24.sp)
                Text("Height: ${details.height} decimeters")
                Text("Weight: ${details.weight} hectograms")
                Text("Types: ${details.types.joinToString(", ") { it.type.name.capitalize() }}")
                Text("Abilities: ${details.abilities.joinToString(", ") { it.ability.name.capitalize() }}")
                Text(
                    "Moves: ${
                        details.moves.take(10).joinToString(", ") { it.move.name.capitalize() }
                    }"
                )

                Button(
                    onClick = {
                        isFavorite = !isFavorite

                        coroutineScope.launch {
                            if (isFavorite) {
                                val pokemonEntity = PokemonEntity(
                                    name = details.name,
                                    height = details.height,
                                    weight = details.weight,
                                    types = details.types.joinToString(", ") { it.type.name },
                                    abilities = details.abilities.joinToString(", ") { it.ability.name },
                                    moves = details.moves.take(10)
                                        .joinToString(", ") { it.move.name }
                                )

                                pokemonDao.insertPokemon(pokemonEntity)
                                println("${details.name} ${"Agregado a favoritos"}")
                            } else {
                                pokemonDao.deletePokemonByName(details.name)
                                println("${details.name} ${"Eliminado de favoritos"}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
fun FavoritesScreen(navController: NavController) {
    val pokemonDao = DatabaseClient.getInstance(LocalContext.current).pokemonDao()
    var pokemonList by remember { mutableStateOf<List<PokemonEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadPokemonList() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            val allPokemons =
                pokemonDao.getAllPokemons()
            pokemonList = allPokemons
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadPokemonList()
    }

    Scaffold(
        topBar = { TopBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                Text("Cargando...")
            } else if (pokemonList.isEmpty()) {
                Text("No tienes favoritos.", modifier = Modifier.padding(8.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    pokemonList.forEach { pokemon ->
                        Button(
                            onClick = {
                                navController.navigate("pokemonDetail/${pokemon.name}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(text = pokemon.name.capitalize())
                        }
                    }
                }
            }
        }
    }
}