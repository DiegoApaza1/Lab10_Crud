package com.tecsup.lab10_crud.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tecsup.lab10_crud.data.BookApiService
import com.tecsup.lab10_crud.data.BookModel
import kotlinx.coroutines.launch

@Composable
fun ContenidoBooksListado(navController: NavHostController, servicio: BookApiService) {
    var listaBooks = remember { mutableStateListOf<BookModel>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                refreshBooksList(servicio, listaBooks)
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ID", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                    Text("BOOK", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                    Text("Acción", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
                }
            }

            items(listaBooks) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.id}", modifier = Modifier.weight(0.1f))
                    Text(item.title, modifier = Modifier.weight(0.6f))
                    IconButton(
                        onClick = { navController.navigate("bookVer/${item.id}") },
                        modifier = Modifier.weight(0.1f)
                    ) {
                        Icon(Icons.Outlined.Edit, "Editar")
                    }
                    IconButton(
                        onClick = { navController.navigate("bookDel/${item.id}") },
                        modifier = Modifier.weight(0.1f)
                    ) {
                        Icon(Icons.Outlined.Delete, "Eliminar")
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoBookEditar(navController: NavHostController, servicio: BookApiService, pid: Int = 0) {
    var id by remember { mutableStateOf(pid) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var publication_date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pid) {
        coroutineScope.launch {
            if (pid != 0) {
                isLoading = true
                errorMessage = null
                try {
                    val response = servicio.selectBook(pid.toString())
                    if (response.isSuccessful) {
                        response.body()?.let { book ->
                            title = book.title
                            author = book.author
                            publication_date = book.publication_date
                            category = book.category
                        }
                    } else {
                        errorMessage = "Error al cargar el libro: ${response.errorBody()?.string()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        TextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        TextField(
            value = publication_date,
            onValueChange = { publication_date = it },
            label = { Text("Fecha de Publicación") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        val book = BookModel(id, title, author, publication_date, category)
                        val response = if (id == 0) {
                            servicio.insertBook(book)
                        } else {
                            servicio.updateBook(id.toString(), book)
                        }

                        if (response.isSuccessful) {
                            Log.d("API", "Book ${if (id == 0) "added" else "updated"} successfully")
                            navController.navigate("books")
                        } else {
                            errorMessage = "Error: ${response.errorBody()?.string()}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Procesando..." else "Guardar")
        }
    }
}

@Composable
fun ContenidoBookEliminar(navController: NavHostController, servicio: BookApiService, id: Int) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(id) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = servicio.deleteBook(id.toString())
                if (response.isSuccessful) {
                    Log.d("API", "Book deleted successfully")
                    navController.navigate("books")
                } else {
                    errorMessage = "Error al eliminar el libro: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
    }
}

suspend fun refreshBooksList(servicio: BookApiService, listaBooks: SnapshotStateList<BookModel>) {
    try {
        val response = servicio.selectBooks() // Corregir selectBook a selectBooks
        listaBooks.clear()
        listaBooks.addAll(response)
    } catch (e: Exception) {
        Log.e("API", "Error refreshing books list: ${e.message}")
    }
}
