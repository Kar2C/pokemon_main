package com.example.pokemon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.pokemon.driverAdapters.PokemonDriverAdapter
import com.example.pokemon.services.models.PokemonEntry
import com.example.pokemon.services.models.PokemonResponse
import com.example.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.launch
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class) // Añadimos esta línea para la API experimental
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonTheme {
                PokedexScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen() {
    // Variables para manejar el estado de los datos
    val coroutineScope = rememberCoroutineScope()

    // Estado de la lista de Pokémon y el estado de carga/error
    var pokemonList by remember { mutableStateOf<List<PokemonEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Llamada a la API dentro de un LaunchedEffect para hacer la petición al cargar la pantalla

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val response: Response<PokemonResponse> = PokemonDriverAdapter.api.getPokedex()
                if (response.isSuccessful) {
                    pokemonList = response.body()?.pokemon_entries ?: emptyList()
                } else {
                    errorMessage = "Error en la respuesta de la API"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    // Composición de la UI
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar()
            if (isLoading) {
                // Mostrar indicador de carga mientras se obtienen los datos
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Mostrar un mensaje de error si ocurrió algún problema
                errorMessage?.let {
                    Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
                }
                // Mostrar la lista de Pokémon
                PokemonGrid(pokemonList)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF44336)) // Color rojo estilo Pokédex
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon(
            //painter = painterResource(id = R.drawable.ic_pokeball), // Icono de Pokebola
            //contentDescription = "Pokeball",
        //modifier = Modifier.padding(end = 8.dp)
        //)
        Text(
            text = "Pokedex",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.weight(1f)
        )
        //Icon(
          //  painter = painterResource(id = R.drawable.ic_menu), // Icono de menú
            //contentDescription = "Menu",
            //modifier = Modifier.padding(start = 8.dp)
        //)
    }
}

@Composable
fun SearchBar() {
    val searchText = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFEEEEEE), CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        BasicTextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black)
        )
    }
}

@Composable
fun PokemonGrid(pokemonList: List<PokemonEntry>) {
    val coroutineScope = rememberCoroutineScope()
    var pokemonDetails by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(8.dp)
    ) {
        items(pokemonList.size) { index ->
            val pokemon = pokemonList[index]

            // Cargar los detalles del Pokémon (sprites) cuando sea necesario
            LaunchedEffect(pokemon.entry_number) {
                if (pokemonDetails[pokemon.entry_number] == null) {
                    val response = PokemonDriverAdapter.api.getPokemonDetails(pokemon.entry_number)
                    if (response.isSuccessful) {
                        // Convertir el mapa a mutable y agregar el detalle
                        pokemonDetails = pokemonDetails.toMutableMap().apply {
                            put(pokemon.entry_number, response.body()?.sprites?.front_default ?: "")
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .background(Color.Gray)
            ) {
                // Usar la imagen desde el mapa de detalles del Pokémon
                val imageUrl = pokemonDetails[pokemon.entry_number]
                    ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.entry_number}.png"

                // Cargar la imagen usando Coil
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = pokemon.pokemon_species.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF44336)) // Color rojo estilo Pokédex
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Acción de Home */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
                tint = Color.White
            )
        }
        IconButton(onClick = { /* Acción de Favoritos */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favoritos",
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokedexScreenPreview() {
    PokemonTheme {
        PokedexScreen()
    }
}
