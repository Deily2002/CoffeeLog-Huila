package com.soto.coffeelog_huila.ui.lotes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.soto.coffeelog_huila.data.CalidadSCA
import com.soto.coffeelog_huila.data.LoteEntity
import com.soto.coffeelog_huila.ui.home.CustomBottomNavigation
import com.soto.coffeelog_huila.ui.theme.AccentGreen
import com.soto.coffeelog_huila.ui.theme.BackgroundCrema
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesListScreen(navController: NavController, viewModel: LotViewModel) {
    val listaLotes by viewModel.lotes.collectAsState(initial = emptyList())

    // Estados para búsqueda y filtrado
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var loteAEliminar by remember { mutableStateOf<LoteEntity?>(null) }

    // 1. EXTRAMOS LOS AÑOS ÚNICOS BASADOS EN LA FECHA DEL SISTEMA (fechaRegistro)
    val formatterAnio = SimpleDateFormat("yyyy", Locale.getDefault())
    val añosDisponibles = listaLotes.map { formatterAnio.format(Date(it.fechaCosecha)) }.distinct().sortedDescending()

    // 2. LÓGICA DE FILTRADO DINÁMICO
    val lotesFiltrados = listaLotes.filter { lote ->
        val anioRegistro = formatterAnio.format(Date(lote.fechaCosecha))

        // Coincide con la búsqueda (por nombre, variedad o año)
        val coincideBusqueda = lote.numeroLote.contains(searchQuery, ignoreCase = true) ||
                lote.variedad.contains(searchQuery, ignoreCase = true) ||
                anioRegistro.contains(searchQuery)

        // Coincide con el chip de filtro
        val coincideFiltro = if (selectedFilter == "Todos") true else anioRegistro == selectedFilter

        coincideBusqueda && coincideFiltro
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lotes de café", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("nuevo_lote") }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Lote", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeDark)
                )
            },
            // AQUI LE PASAMOS LA PANTALLA ACTUAL AL MENÚ
            bottomBar = { CustomBottomNavigation(navController, currentScreen = "lotes") },
            containerColor = BackgroundCrema
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // BARRA DE BÚSQUEDA REACTIVA
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar lote o año...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = { Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = CoffeeDark) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent, focusedBorderColor = CoffeeDark
                    ),
                    singleLine = true
                )

                // CHIPS DE FILTRO DINÁMICOS
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChipItem("Todos", selectedFilter == "Todos") { selectedFilter = "Todos" }
                    }
                    items(añosDisponibles) { anio ->
                        FilterChipItem(anio, selectedFilter == anio) { selectedFilter = anio }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LISTA DE LOTES
                if (lotesFiltrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱", fontSize = 50.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(if (searchQuery.isNotEmpty()) "No hay resultados" else "No hay lotes ingresados", color = CoffeeDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lotesFiltrados) { lote ->
                            LoteCardItem(
                                lote = lote,
                                viewModel = viewModel,
                                onVer = { navController.navigate("detalle_lote/${lote.id}") },
                                onEditar = { navController.navigate("editar_lote/${lote.id}") },
                                onEliminar = { loteAEliminar = lote }
                            )
                        }
                    }
                }
            }
        }

        // MENSAJE DE ÉXITO Y MODAL DE BORRAR
        if (viewModel.snackbarMessage != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp, start = 16.dp, end = 16.dp).systemBarsPadding(),
                containerColor = AccentGreen, contentColor = Color.White
            ) { Text(viewModel.snackbarMessage!!, fontWeight = FontWeight.Bold) }
        }

        if (loteAEliminar != null) {
            AlertDialog(
                onDismissRequest = { loteAEliminar = null },
                title = { Text("Eliminar Lote", color = CoffeeDark, fontWeight = FontWeight.Bold) },
                text = { Text("¿Está seguro de que desea eliminar el ${loteAEliminar?.numeroLote}? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.eliminarLote(loteAEliminar!!); loteAEliminar = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Sí, eliminar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { loteAEliminar = null }) { Text("No, cancelar", color = CoffeeDark) }
                },
                containerColor = BackgroundCrema
            )
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(if (isSelected) CoffeeDark else Color.White, RoundedCornerShape(20.dp))
            .border(1.dp, if (isSelected) CoffeeDark else Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) { Text(text, color = if (isSelected) Color.White else CoffeeDark, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
}

@Composable
fun LoteCardItem(
    lote: LoteEntity,
    viewModel: LotViewModel,
    onVer: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    // 1. TRAEMOS LAS CATACIONES DE ESTE LOTE EN TIEMPO REAL
    val cataciones by viewModel.obtenerCatacionesDeLote(lote.id).collectAsState(initial = emptyList())
    val tieneCataciones = cataciones.isNotEmpty()

    // 2. BUSCAMOS LA MEJOR CATACIÓN
    val mejorCatacion = cataciones.maxByOrNull { it.puntajeTotal }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            // FOTO DEL LOTE
            Box(
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEFEBE9)),
                contentAlignment = Alignment.Center
            ) {
                if (lote.imagenUri != null) {
                    AsyncImage(model = lote.imagenUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Outlined.Eco, contentDescription = null, tint = CoffeeDark)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // INFORMACIÓN DEL LOTE
            Column(modifier = Modifier.weight(1f)) {

                // Fila 1: Título y Etiqueta (Badge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(lote.numeroLote, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CoffeeDark)

                    // LÓGICA DE LA ETIQUETA
                    if (tieneCataciones && mejorCatacion != null) {
                        BadgeCalidad(mejorCatacion.calidadSugerida)
                    } else {
                        BadgePendiente()
                    }
                }

                Text("${lote.variedad} · ${lote.proceso.name.lowercase().replaceFirstChar { it.uppercase() }}", color = Color.DarkGray, fontSize = 13.sp)

                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(lote.fechaCosecha))
                Text("Cosecha: $fecha", color = Color.Gray, fontSize = 12.sp)

                // Fila 2: Puntaje y Botones de Acción
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                    // LÓGICA DEL PUNTAJE / SIN CATAR
                    if (tieneCataciones && mejorCatacion != null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("Puntaje: ", color = Color.Gray, fontSize = 13.sp)
                            Text("${mejorCatacion.puntajeTotal} pts", color = CoffeeDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else {
                        Text(
                            text = "Sin catar",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // ÍCONOS DE ACCIÓN (Ojo, Lápiz, Caneca)
                    Row {
                        IconButton(onClick = onVer, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Visibility, contentDescription = "Ver", tint = CoffeeDark, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onEditar, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = CoffeeDark, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onEliminar, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = CoffeeDark, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// COMPONENTE: Etiqueta de Pendiente
@Composable
fun BadgePendiente() {
    Row(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.HourglassEmpty, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Pendiente", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}