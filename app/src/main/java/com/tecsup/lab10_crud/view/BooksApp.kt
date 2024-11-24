package com.tecsup.lab10_crud.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lab10_crud.data.BookApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ROUTE_INICIO = "inicio"
private const val ROUTE_BOOKS = "books"
private const val ROUTE_BOOK_NUEVO = "bookNuevo"
private const val ROUTE_BOOK_VER = "bookVer/{id}"
private const val ROUTE_BOOK_DEL = "bookDel/{id}"

@Composable
fun BooksApp() {
    val urlBase = "http://10.0.2.2:8000/" // Cambiar por tu IP si usas dispositivo físico
    val retrofit = Retrofit.Builder().baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val servicio = retrofit.create(BookApiService::class.java)
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.padding(top = 40.dp),
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        floatingActionButton = { BotonFAB(navController) },
        content = { paddingValues -> Contenido(paddingValues, navController, servicio) }
    )
}

@Composable
fun BotonFAB(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route
    if (rutaActual == ROUTE_BOOKS) {
        FloatingActionButton(
            containerColor = Color.Magenta,
            contentColor = Color.White,
            onClick = { navController.navigate(ROUTE_BOOK_NUEVO) }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "BOOKS APP",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    NavigationBar(
        containerColor = Color.LightGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = cbeState?.destination?.route == ROUTE_INICIO,
            onClick = { navController.navigate(ROUTE_INICIO) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Books") },
            label = { Text("Books") },
            selected = cbeState?.destination?.route == ROUTE_BOOKS,
            onClick = { navController.navigate(ROUTE_BOOKS) }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicio: BookApiService
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_INICIO
        ) {
            composable(ROUTE_INICIO) { ScreenInicio() }
            composable(ROUTE_BOOKS) { ContenidoBooksListado(navController, servicio) }
            composable(ROUTE_BOOK_NUEVO) {
                ContenidoBookEditar(navController, servicio, 0)
            }
            composable(ROUTE_BOOK_VER, arguments = listOf(
                navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoBookEditar(navController, servicio, it.arguments!!.getInt("id"))
            }
            composable(ROUTE_BOOK_DEL, arguments = listOf(
                navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoBookEliminar(navController, servicio, it.arguments!!.getInt("id"))
            }
        }
    }
}

@Composable
fun ScreenInicio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a Books App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Explora y gestiona tus libros favoritos",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { /* Agregar acción si es necesario */ }
        ) {
            Text("Comenzar")
        }
    }
}
