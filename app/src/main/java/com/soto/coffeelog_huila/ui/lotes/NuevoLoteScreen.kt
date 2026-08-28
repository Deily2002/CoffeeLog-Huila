package com.soto.coffeelog_huila.ui.lotes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.soto.coffeelog_huila.data.ProcesoCafe
import com.soto.coffeelog_huila.data.SessionManager
import com.soto.coffeelog_huila.ui.home.CustomBottomNavigation
import com.soto.coffeelog_huila.ui.theme.BackgroundCrema
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoLoteScreen(navController: NavController, viewModel: LotViewModel, loteIdParaEditar: Long? = null) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Estados
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var nombreFinca by remember { mutableStateOf("") }
    var nombreLote by remember { mutableStateOf("") }
    var altitud by remember { mutableStateOf("") }
    var edadArboles by remember { mutableStateOf("") }
    var produccion by remember { mutableStateOf("") }
    var factor by remember { mutableStateOf("") }
    var humedad by remember { mutableStateOf("") }
    var pesoTotal by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var fechaTexto by remember { mutableStateOf("Seleccionar fecha") }
    var fechaMilisegundos by remember { mutableStateOf(System.currentTimeMillis()) }
    var variedadSeleccionada by remember { mutableStateOf("Seleccionar variedad") }
    var procesoSeleccionado by remember { mutableStateOf(ProcesoCafe.LAVADO) }

    val tituloPantalla = if (loteIdParaEditar != null) "Editar Lote" else "Nuevo Lote"

    // Si viene un ID para editar, cargamos los datos de la Base de Datos
    LaunchedEffect(loteIdParaEditar) {
        if (loteIdParaEditar != null) {
            val loteViejo = viewModel.obtenerLote(loteIdParaEditar)
            if (loteViejo != null) {
                val fincaVieja = viewModel.obtenerFinca(loteViejo.fincaId)

                nombreFinca = fincaVieja?.nombre ?: ""
                nombreLote = loteViejo.numeroLote
                altitud = loteViejo.altitud?.toString() ?: ""
                edadArboles = loteViejo.edadArboles?.toString() ?: ""
                produccion = loteViejo.produccionAnual?.toString() ?: ""
                factor = loteViejo.factorRendimiento?.toString() ?: ""
                humedad = loteViejo.humedad?.toString() ?: ""
                pesoTotal = loteViejo.pesoTotal.toString()
                notas = loteViejo.notasAdicionales ?: ""
                variedadSeleccionada = loteViejo.variedad
                procesoSeleccionado = loteViejo.proceso
                fechaMilisegundos = loteViejo.fechaCosecha

                if (loteViejo.imagenUri != null) {
                    imageUri = Uri.parse(loteViejo.imagenUri)
                }

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaTexto = formatter.format(Date(loteViejo.fechaCosecha))
            }
        }
    }

    // LANZADOR DE GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val uriPermanente = guardarImagenLocalmente(context, uri)
            imageUri = uriPermanente ?: uri
        }
    }

    // Dropdowns y DatePicker states
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaMilisegundos)
    var variedadExpanded by remember { mutableStateOf(false) }
    val opcionesVariedad = listOf("Castillo", "Caturra", "Borbón", "Geisha", "Colombia", "Pink Bourbon")
    var procesoExpanded by remember { mutableStateOf(false) }
    val opcionesProceso = ProcesoCafe.entries

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
        unfocusedBorderColor = Color(0xFFE0E0E0), focusedBorderColor = CoffeeDark
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloPantalla, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CoffeeDark)
            )
        },
        bottomBar = { CustomBottomNavigation(navController, currentScreen = "lotes") },
        containerColor = BackgroundCrema
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO Y DATOS BÁSICOS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.weight(0.4f).aspectRatio(1f).background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)).clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = CoffeeDark, modifier = Modifier.size(32.dp))
                            Text("Foto", fontSize = 12.sp, color = CoffeeDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(modifier = Modifier.weight(0.6f)) {
                    OutlinedTextField(value = nombreFinca, onValueChange = { nombreFinca = it }, label = { Text("Finca *") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = nombreLote, onValueChange = { nombreLote = it }, label = { Text("Número de Lote *") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FECHA Y VARIEDAD
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { mostrarCalendario = true }) {
                    OutlinedTextField(value = fechaTexto, onValueChange = { }, enabled = false, label = { Text("Fecha Cosecha *") }, leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                }

                ExposedDropdownMenuBox(expanded = variedadExpanded, onExpandedChange = { variedadExpanded = !variedadExpanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = variedadSeleccionada, onValueChange = {}, readOnly = true, label = { Text("Variedad *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = variedadExpanded) }, modifier = Modifier.menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    ExposedDropdownMenu(expanded = variedadExpanded, onDismissRequest = { variedadExpanded = false }) {
                        opcionesVariedad.forEach { opcion -> DropdownMenuItem(text = { Text(opcion) }, onClick = { variedadSeleccionada = opcion; variedadExpanded = false }) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ALTITUD Y EDAD
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = altitud, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) altitud = it }, label = { Text("Altitud") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                OutlinedTextField(value = edadArboles, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) edadArboles = it }, label = { Text("Edad árboles") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = produccion, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) produccion = it }, label = { Text("Producción Anual Finca (Kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)

            Spacer(modifier = Modifier.height(30.dp))

            // TÍTULO 2: INFORMACIÓN DEL PROCESO
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Coffee, contentDescription = null, tint = CoffeeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Información del proceso", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // PROCESO Y RENDIMIENTO
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(expanded = procesoExpanded, onExpandedChange = { procesoExpanded = !procesoExpanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = procesoSeleccionado.name, onValueChange = {}, readOnly = true, label = { Text("Proceso *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = procesoExpanded) }, modifier = Modifier.menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    ExposedDropdownMenu(expanded = procesoExpanded, onDismissRequest = { procesoExpanded = false }) {
                        opcionesProceso.forEach { opcion -> DropdownMenuItem(text = { Text(opcion.name) }, onClick = { procesoSeleccionado = opcion; procesoExpanded = false }) }
                    }
                }
                OutlinedTextField(value = factor, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) factor = it }, label = { Text("Rendimiento %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = humedad, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) humedad = it }, label = { Text("Humedad %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                OutlinedTextField(value = pesoTotal, onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) pesoTotal = it }, label = { Text("Peso (Kg) *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas adicionales") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences), modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), colors = fieldColors, maxLines = 3)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (nombreFinca.isNotEmpty() && nombreLote.isNotEmpty() && variedadSeleccionada != "Seleccionar variedad") {
                        viewModel.guardarLoteCompleto(
                            loteId = loteIdParaEditar,
                            nombreFinca = nombreFinca, numeroLote = nombreLote, variedad = variedadSeleccionada,
                            proceso = procesoSeleccionado, fecha = fechaMilisegundos, altitud = altitud,
                            edadArboles = edadArboles, produccion = produccion, factor = factor,
                            humedad = humedad, pesoTotal = pesoTotal, notas = notas,
                            usuarioId = sessionManager.getUserId(), imagenUri = imageUri?.toString(),
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)
            ) {
                Text(if (loteIdParaEditar != null) "Actualizar Lote" else "Guardar Lote", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        if (mostrarCalendario) {
            DatePickerDialog(
                onDismissRequest = { mostrarCalendario = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaMilisegundos = millis
                            fechaTexto = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
                        }
                        mostrarCalendario = false
                    }) { Text("Aceptar", color = CoffeeDark, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar", color = Color.Gray) } }
            ) { DatePicker(state = datePickerState) }
        }
    }
}

// FUNCIÓN PARA GUARDAR LA FOTO PERMANENTEMENTE EN LA APP
fun guardarImagenLocalmente(context: android.content.Context, uri: android.net.Uri): android.net.Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = java.io.File(context.filesDir, "lote_${System.currentTimeMillis()}.jpg")
        val outputStream = java.io.FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        android.net.Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}