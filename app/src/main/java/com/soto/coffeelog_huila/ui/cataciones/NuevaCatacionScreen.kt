package com.soto.coffeelog_huila.ui.cataciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.soto.coffeelog_huila.data.CalidadSCA
import com.soto.coffeelog_huila.data.LoteEntity
import com.soto.coffeelog_huila.ui.theme.AccentGreen
import com.soto.coffeelog_huila.ui.theme.BackgroundCrema
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import kotlinx.coroutines.launch
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCatacionScreen(navController: NavController, viewModel: CatacionViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva catación", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeDark)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundCrema
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // EL STEPPER
            StepperUI(pasoActual = viewModel.pasoActual)

            Spacer(modifier = Modifier.height(24.dp))

            // CONTROL DEL FLUJO (Pestañas)
            when (viewModel.pasoActual) {
                0 -> PasoInformacion(viewModel)
                1 -> PasoAtributos(viewModel)
                2 -> PasoNotas(viewModel, onGuardar = {
                    scope.launch {
                        snackbarHostState.showSnackbar("¡Catación guardada correctamente!")
                        navController.popBackStack() // Volver a la pantalla anterior
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasoInformacion(viewModel: CatacionViewModel) {
    val lotesList by viewModel.lotesDisponibles.collectAsState(initial = emptyList())
    var menuLotesExpandido by remember { mutableStateOf(false) }
    var menuTuesteExpandido by remember { mutableStateOf(false) }

    // Variables para el Calendario
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Información de la catación", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
        Text("Completa los datos generales", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        // 1. DESPLEGABLE DE LOTES
        ExposedDropdownMenuBox(
            expanded = menuLotesExpandido,
            onExpandedChange = { menuLotesExpandido = !menuLotesExpandido }
        ) {
            val nombreLote = viewModel.loteSeleccionado?.let { "${it.numeroLote} - ${it.variedad}" } ?: "Seleccione un lote..."
            OutlinedTextField(
                value = nombreLote,
                onValueChange = {},
                readOnly = true,
                label = { Text("Lote *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuLotesExpandido) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = menuLotesExpandido, onDismissRequest = { menuLotesExpandido = false }) {
                lotesList.forEach { lote ->
                    DropdownMenuItem(
                        text = { Text("${lote.numeroLote} - ${lote.variedad}") },
                        onClick = {
                            viewModel.loteSeleccionado = lote
                            menuLotesExpandido = false
                        }
                    )
                }
            }
        }

        // 2. TARJETA RESUMEN
        if (viewModel.loteSeleccionado != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Eco, null, tint = AccentGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    // Traemos el proceso y el peso dinámicamente
                    Text("Finca #${viewModel.loteSeleccionado!!.fincaId} · ${viewModel.loteSeleccionado!!.proceso.name} · ${viewModel.loteSeleccionado!!.pesoTotal} Kg", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. CAMPO CATADOR
        OutlinedTextField(
            value = viewModel.catadorNombre,
            onValueChange = { viewModel.catadorNombre = it },
            label = { Text("Catador *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. CAMPO FECHA
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = viewModel.fechaCatacion,
                onValueChange = { },
                readOnly = true, // Evita que se abra el teclado normal
                label = { Text("Fecha de catación *") },
                leadingIcon = { Icon(Icons.Outlined.CalendarToday, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { showDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. DESPLEGABLE DE TUESTE
        val tuestes = listOf("Claro", "Medio", "Oscuro")
        ExposedDropdownMenuBox(expanded = menuTuesteExpandido, onExpandedChange = { menuTuesteExpandido = !menuTuesteExpandido }) {
            OutlinedTextField(
                value = viewModel.nivelTueste, onValueChange = {}, readOnly = true,
                label = { Text("Nivel de tueste *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTuesteExpandido) },
                modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = menuTuesteExpandido, onDismissRequest = { menuTuesteExpandido = false }) {
                tuestes.forEach { tueste ->
                    DropdownMenuItem(text = { Text(tueste) }, onClick = { viewModel.nivelTueste = tueste; menuTuesteExpandido = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 6. BOTÓN SIGUIENTE
        Button(
            onClick = { if (viewModel.loteSeleccionado != null) viewModel.pasoActual = 1 },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark, disabledContainerColor = Color.LightGray),
            enabled = viewModel.loteSeleccionado != null,
            shape = RoundedCornerShape(12.dp)
        ) { Text("Siguiente", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }

    // ==========================================
    // DIÁLOGO DEL CALENDARIO (Aparece flotando)
    // ==========================================
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Formateamos la fecha seleccionada
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formatter.timeZone = TimeZone.getTimeZone("UTC") // Evita desfases de zona horaria
                        viewModel.fechaCatacion = formatter.format(Date(millis))
                    }
                }) {
                    Text("Aceptar", color = CoffeeDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = CoffeeDark)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = BackgroundCrema)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = CoffeeDark,
                    todayDateBorderColor = CoffeeDark,
                    todayContentColor = CoffeeDark
                )
            )
        }
    }
}

@Composable
fun PasoAtributos(viewModel: CatacionViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Puntuar atributos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
        Text("(Escala SCA 0 - 10)", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        // AQUÍ AGREGAMOS UN ÍCONO DIFERENTE PARA CADA ATRIBUTO
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            AtributoSlider(Icons.Outlined.LocalFlorist, "Fragancia/Aroma", viewModel.fragancia) { viewModel.fragancia = it }
            AtributoSlider(Icons.Outlined.Restaurant, "Sabor", viewModel.sabor) { viewModel.sabor = it } // Usamos cubiertos para el sabor
            AtributoSlider(Icons.Outlined.WaterDrop, "Sabor Residual", viewModel.saborResidual) { viewModel.saborResidual = it }
            AtributoSlider(Icons.Outlined.Tonality, "Acidez", viewModel.acidez) { viewModel.acidez = it } // Círculo que parece un cítrico
            AtributoSlider(Icons.Outlined.FitnessCenter, "Cuerpo", viewModel.cuerpo) { viewModel.cuerpo = it } // Pesa para el "cuerpo"
            AtributoSlider(Icons.Outlined.Balance, "Balance", viewModel.balance) { viewModel.balance = it }
            AtributoSlider(Icons.Outlined.Waves, "Uniformidad", viewModel.uniformidad) { viewModel.uniformidad = it }
            AtributoSlider(Icons.Outlined.AutoAwesome, "Taza Limpia", viewModel.tazaLimpia) { viewModel.tazaLimpia = it } // Estrellas mágicas
            AtributoSlider(Icons.Outlined.LocalCafe, "Dulzor", viewModel.dulzor) { viewModel.dulzor = it }
            AtributoSlider(Icons.Outlined.PersonOutline, "Puntaje Catador", viewModel.puntajeCatador) { viewModel.puntajeCatador = it }
        }

        // TARJETA DE PUNTAJE TOTAL CON COLOR DINÁMICO
        val colorEtiqueta = when (viewModel.calidadEvaluada) {
            com.soto.coffeelog_huila.data.CalidadSCA.EXCELENTE -> Color(0xFF2E7D32)
            com.soto.coffeelog_huila.data.CalidadSCA.MUY_BUENA -> AccentGreen
            com.soto.coffeelog_huila.data.CalidadSCA.BUENA -> Color(0xFFE65100)
            com.soto.coffeelog_huila.data.CalidadSCA.REGULAR -> Color.Red
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Puntaje Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CoffeeDark)
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(String.format("%.2f", viewModel.puntajeTotal), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = colorEtiqueta)
                        Text(" pts", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Text(viewModel.calidadEvaluada.name.replace("_", " "), color = colorEtiqueta, fontWeight = FontWeight.Bold)
                }
            }
        }

        Row {
            OutlinedButton(onClick = { viewModel.pasoActual = 0 }, modifier = Modifier.weight(1f).height(55.dp), shape = RoundedCornerShape(12.dp)) { Text("Atrás", color = CoffeeDark) }
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = { viewModel.pasoActual = 2 }, modifier = Modifier.weight(1f).height(55.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)) {
                Text("Siguiente", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// === ¡OJO AQUÍ! MODIFICAMOS EL COMPONENTE PARA QUE RECIBA EL ÍCONO ===
@Composable
fun AtributoSlider(icon: androidx.compose.ui.graphics.vector.ImageVector, nombre: String, valor: Float, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        // AQUÍ REEMPLAZAMOS LA ESTRELLA POR LA VARIABLE "icon"
        Icon(icon, contentDescription = null, tint = CoffeeDark, modifier = Modifier.size(18.dp))
        Text(nombre, modifier = Modifier.width(115.dp).padding(start = 8.dp), color = CoffeeDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Slider(
            value = valor,
            onValueChange = { onValueChange(Math.round(it * 4) / 4f) },
            valueRange = 6f..10f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = CoffeeDark, activeTrackColor = CoffeeDark)
        )
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
            Text(String.format("%.2f", valor), modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 13.sp)
        }
    }
}

@Composable
fun PasoNotas(viewModel: CatacionViewModel, onGuardar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        Text("Notas y observaciones", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.notas,
            onValueChange = { viewModel.notas = it },
            label = { Text("Notas sensoriales (Opcional)") },
            placeholder = { Text("Ej: Acidez cítrica, cuerpo cremoso, notas a panela...") },
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        Row {
            OutlinedButton(onClick = { viewModel.pasoActual = 1 }, modifier = Modifier.weight(1f).height(55.dp), shape = RoundedCornerShape(12.dp)) { Text("Atrás", color = CoffeeDark) }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    viewModel.guardarCatacion(onSuccess = onGuardar)
                },
                modifier = Modifier.weight(1f).height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- UI REUTILIZABLE ---
@Composable
fun AtributoSlider(nombre: String, valor: Float, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Icon(Icons.Outlined.StarOutline, null, tint = CoffeeDark, modifier = Modifier.size(18.dp))
        Text(nombre, modifier = Modifier.width(115.dp).padding(start = 8.dp), color = CoffeeDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Slider(
            value = valor,
            onValueChange = { onValueChange(Math.round(it * 4) / 4f) }, // Obliga a saltos de 0.25 (Ej: 8.25, 8.50)
            valueRange = 6f..10f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = CoffeeDark, activeTrackColor = CoffeeDark)
        )
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
            Text(String.format("%.2f", valor), modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 13.sp)
        }
    }
}

@Composable
fun StepperUI(pasoActual: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        StepItem(Icons.Outlined.Info, "Información", isActive = pasoActual >= 0)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = if(pasoActual >= 1) CoffeeDark else Color.LightGray)
        StepItem(Icons.Outlined.Tune, "Atributos", isActive = pasoActual >= 1)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = if(pasoActual >= 2) CoffeeDark else Color.LightGray)
        StepItem(Icons.Outlined.StickyNote2, "Notas", isActive = pasoActual >= 2)
    }
}

@Composable
fun StepItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isActive) CoffeeDark else Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = if (isActive) Color.White else Color.Gray, modifier = Modifier.size(20.dp))
        }
        Text(label, fontSize = 11.sp, fontWeight = if(isActive) FontWeight.Bold else FontWeight.Normal, color = if (isActive) CoffeeDark else Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}