package com.soto.coffeelog_huila.ui.lotes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.soto.coffeelog_huila.data.CalidadSCA
import com.soto.coffeelog_huila.data.CatacionEntity
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
fun DetalleLoteScreen(navController: NavController, viewModel: LotViewModel, loteId: Long) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var lote by remember { mutableStateOf<LoteEntity?>(null) }
    var nombreFinca by remember { mutableStateOf("Cargando...") }

    // Traemos la lista de cataciones de este lote en tiempo real
    val listaCataciones by viewModel.obtenerCatacionesDeLote(loteId).collectAsState(initial = emptyList())

    // Cargar datos de la BD al abrir la pantalla
    LaunchedEffect(loteId) {
        lote = viewModel.obtenerLote(loteId)
        lote?.let {
            val finca = viewModel.obtenerFinca(it.fincaId)
            nombreFinca = finca?.nombre ?: "Sin Finca"
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundCrema)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del lote", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Opciones extra */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeDark)
                )
            },
            bottomBar = { CustomBottomNavigation(navController, "lotes") },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("nueva_catacion") },
                    containerColor = CoffeeDark
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Catar", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            lote?.let { elLote ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // PESTAÑAS (TABS)
                    TabRow(
                        selectedTabIndex = tabSeleccionada,
                        containerColor = BackgroundCrema,
                        contentColor = CoffeeDark,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[tabSeleccionada]),
                                color = CoffeeDark,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("Detalle del Lote", fontWeight = FontWeight.Bold) })
                        Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("Cataciones (${listaCataciones.size})", fontWeight = FontWeight.Bold) })
                    }

                    // CONTENIDO DE LAS PESTAÑAS
                    if (tabSeleccionada == 0) {
                        // ==========================================
                        // PESTAÑA 1: INFORMACIÓN DEL LOTE
                        // ==========================================
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // TARJETA PRINCIPAL
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column {
                                    // FOTO GRANDE DEL LOTE
                                    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                                        if (elLote.imagenUri != null) {
                                            AsyncImage(
                                                model = elLote.imagenUri,
                                                contentDescription = "Foto Lote",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize().background(Color(0xFFEFEBE9)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(50.dp))
                                            }
                                        }
                                    }

                                    // CONTENIDO BLANCO DE LA TARJETA
                                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                                        // ENCABEZADO
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .background(CoffeeDark)
                                                    .border(2.dp, Color.White, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Outlined.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column {
                                                Text(elLote.numeroLote, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
                                                val anioCosecha = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(elLote.fechaCosecha))
                                                Text("Cosecha $anioCosecha", color = Color.Gray, fontSize = 14.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))
                                        HorizontalDivider(color = Color(0xFFEFEBE9))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // TODOS LOS DATOS CON SUS ÍCONOS
                                        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(elLote.fechaCosecha))

                                        FilaDetalleIcono(Icons.Outlined.Home, "Finca", nombreFinca)
                                        FilaDetalleIcono(Icons.Outlined.CalendarToday, "Fecha Cosecha", formatoFecha)
                                        FilaDetalleIcono(Icons.Outlined.Eco, "Variedad", elLote.variedad)
                                        FilaDetalleIcono(Icons.Outlined.WaterDrop, "Proceso", elLote.proceso.name.lowercase().replaceFirstChar { it.uppercase() })

                                        elLote.altitud?.let { FilaDetalleIcono(Icons.Outlined.Terrain, "Altitud", "$it m.s.n.m.") }
                                        elLote.factorRendimiento?.let { FilaDetalleIcono(Icons.Outlined.Analytics, "Factor de Rendimiento", "$it") }
                                        elLote.edadArboles?.let { FilaDetalleIcono(Icons.Outlined.Park, "Edad de los árboles", "$it años") }
                                        elLote.produccionAnual?.let { FilaDetalleIcono(Icons.Outlined.Inventory2, "Producción Anual", "$it kg") }
                                        elLote.humedad?.let { FilaDetalleIcono(Icons.Outlined.Waves, "Humedad del grano", "$it %") }

                                        FilaDetalleIcono(Icons.Outlined.Scale, "Peso del Lote", "${elLote.pesoTotal} kg")
                                    }
                                }
                            }

                            // SI HAY NOTAS ADICIONALES
                            if (!elLote.notasAdicionales.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.EditNote, contentDescription = null, tint = CoffeeDark)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Notas adicionales", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(elLote.notasAdicionales, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 20.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    } else {
                        // ==========================================
                        // PESTAÑA 2: LISTA DE CATACIONES
                        // ==========================================
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            if (listaCataciones.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                                    Text("Aún no hay cataciones registradas.", color = Color.Gray)
                                }
                            } else {
                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    itemsIndexed(listaCataciones) { index, catacion ->
                                        TarjetaCatacion(
                                            catacion = catacion,
                                            numero = listaCataciones.size - index,
                                            viewModel = viewModel
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CoffeeDark)
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES DE DISEÑO
// ==========================================

@Composable
fun FilaDetalleIcono(icon: ImageVector, titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CoffeeDark, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(titulo, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(valor, color = CoffeeDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
    HorizontalDivider(color = Color(0xFFEFEBE9), thickness = 1.dp)
}

// AQUÍ CORREGIMOS LA FUNCIÓN PARA QUE RECIBA EL VIEWMODEL Y BUSQUE EL NOMBRE
@Composable
fun TarjetaCatacion(catacion: CatacionEntity, numero: Int, viewModel: LotViewModel) {
    var nombreCatador by remember { mutableStateOf("Buscando...") }

    LaunchedEffect(catacion.usuarioId) {
        nombreCatador = viewModel.obtenerNombreCatador(catacion.usuarioId)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila 1: Título y Etiqueta
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Catación #$numero", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CoffeeDark)
                BadgeCalidad(catacion.calidadSugerida)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila 2: Datos
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Catador:", color = Color.Gray, fontSize = 14.sp)
                Text(nombreCatador, color = CoffeeDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fecha:", color = Color.Gray, fontSize = 14.sp)
                Text(catacion.fechaCatacion, color = CoffeeDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tueste:", color = Color.Gray, fontSize = 14.sp)
                Text(catacion.nivelTueste, color = CoffeeDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEFEBE9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila 3: Puntaje y Botón de ver
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("Puntaje:", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(String.format("%.1f", catacion.puntajeTotal), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CoffeeDark)
                    Text(" pts", color = CoffeeDark, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /* TODO: Ir a pantalla de Detalle Sensorial */ }) {
                    Text("Ver catación", color = CoffeeDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CoffeeDark, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BadgeCalidad(calidad: CalidadSCA) {
    val (bgColor, textColor, icon) = when (calidad) {
        CalidadSCA.EXCELENTE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.Info)
        CalidadSCA.MUY_BUENA -> Triple(Color(0xFFF1F8E9), AccentGreen, Icons.Default.Info)
        CalidadSCA.BUENA -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.Info)
        CalidadSCA.REGULAR -> Triple(Color(0xFFFFEBEE), Color.Red, Icons.Default.Info)
    }

    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(bgColor).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(calidad.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}