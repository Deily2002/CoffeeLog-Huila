package com.soto.coffeelog_huila.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.ui.theme.BackgroundCrema
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

@Composable
fun RegisterScreen(viewModel: AuthViewModel, onNavigateToRole: () -> Unit, onNavigateToLogin: () -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // 1. BOX PRINCIPAL: Actúa como el lienzo base, SIN imePadding para no trabar el teclado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCrema)
    ) {
        // Decoración Superior Derecha
        Image(
            painter = painterResource(id = R.drawable.decoracion_top_right),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(200.dp)
                .offset(x = 25.dp, y = (-20).dp),
            contentScale = ContentScale.Fit
        )

        // 2. FORMULARIO PRINCIPAL Y SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(45.dp))

                Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, color = CoffeeDark, fontWeight = FontWeight.Bold)
                Text("Regístrate en segundos", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(15.dp))

                // FOTO DE PERFIL
                Box(contentAlignment = Alignment.BottomEnd) {

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { galleryLauncher.launch("image/*") }, // Al tocar el círculo, abre galería
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(painterResource(id = R.drawable.ic_camera), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CoffeeDark)
                            .border(4.dp, Color(0xFFFDF8F5), CircleShape)
                            .clickable { galleryLauncher.launch("image/*") }, // Al tocar el lápiz, abre galería
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(id = R.drawable.ic_edit), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Text("Agrega foto de tu finca", color = CoffeeDark, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))

                Spacer(modifier = Modifier.height(15.dp))

                // CONFIGURACIÓN DE COLORES PARA LOS CAMPOS
                val fieldColors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = CoffeeDark
                )

                // CAMPO NOMBRE
                OutlinedTextField(
                    value = viewModel.regNombre,
                    onValueChange = { viewModel.regNombre = it },
                    label = { Text("Nombre completo", color = Color.Gray) },
                    leadingIcon = { Icon(painterResource(id = R.drawable.ic_person), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(5.dp))

                // CAMPO CORREO
                OutlinedTextField(
                    value = viewModel.regCorreo,
                    onValueChange = { viewModel.regCorreo = it },
                    label = { Text("Correo electrónico", color = Color.Gray) },
                    leadingIcon = { Icon(painterResource(id = R.drawable.ic_email), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(5.dp))

                // CAMPO TELÉFONO
                OutlinedTextField(
                    value = viewModel.regTelefono,
                    onValueChange = { viewModel.regTelefono = it },
                    label = { Text("Teléfono (opcional)", color = Color.Gray) },
                    leadingIcon = { Icon(painterResource(id = R.drawable.ic_phone), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(5.dp))

                // CAMPO CONTRASEÑA
                OutlinedTextField(
                    value = viewModel.regPassword,
                    onValueChange = { viewModel.regPassword = it },
                    label = { Text("Contraseña", color = Color.Gray) },
                    leadingIcon = { Icon(painterResource(id = R.drawable.ic_lock), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        val image = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painterResource(id = image), contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TÉRMINOS Y CONDICIONES
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = CoffeeDark)
                    )

                    // Texto interactivo
                    Column {
                        Row {
                            Text("Acepto los ", fontSize = 14.sp, color = Color.Gray)
                            Text(
                                text = "Términos y Condiciones",
                                fontSize = 14.sp,
                                color = CoffeeDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showTermsDialog = true }
                            )
                        }
                        Row {
                            Text("y la ", fontSize = 14.sp, color = Color.Gray)
                            Text(
                                text = "Política de Privacidad",
                                fontSize = 14.sp,
                                color = CoffeeDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showPrivacyDialog = true }
                            )
                        }
                    }
                }

                if (showError) {
                    Text("Debes llenar los campos obligatorios y aceptar los términos", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN CREAR CUENTA
                Button(
                    onClick = {
                        if (viewModel.regNombre.isNotEmpty() && viewModel.regCorreo.isNotEmpty() && viewModel.regPassword.isNotEmpty() && termsAccepted) {
                            showError = false
                            onNavigateToRole()
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)
                ) {
                    Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // IR A LOGIN
                Row {
                    Text("¿Ya tienes cuenta? ", color = Color.Gray)
                    Text("Inicia sesión", color = CoffeeDark, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateToLogin() })
                }


                Spacer(modifier = Modifier.height(5.dp))
            }


            // 3. DECORACIÓN INFERIOR
            Box(modifier = Modifier.fillMaxWidth().height(78.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.decoracion_bottom_left),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(180.dp)
                        .offset(x = (-20).dp, y = 20.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
    // LÓGICA PARA MOSTRAR LOS DIÁLOGOS
    if (showTermsDialog) {
        TermsDialog(onDismiss = { showTermsDialog = false })
    }
    if (showPrivacyDialog) {
        PrivacyDialog(onDismiss = { showPrivacyDialog = false })
    }
}

@Composable
fun TermsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold, color = CoffeeDark) },
        text = {
            Text(
                "Bienvenido a CoffeeLog.\n\n" +
                        "1. El uso de esta aplicación está destinado exclusivamente a la gestión de datos agronómicos.\n" +
                        "2. Usted es el único responsable de la veracidad de la información ingresada en las Fichas Técnicas.\n" +
                        "3. CoffeeLog no interviene en las transacciones comerciales ni define los precios de venta de su café.\n" +
                        "4. La licencia de la versión PRO es intransferible y de uso anual."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Entendido", color = CoffeeDark, fontWeight = FontWeight.Bold) }
        },
        containerColor = Color(0xFFFDF8F5) // Fondo crema
    )
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Política de Privacidad (Habeas Data)", fontWeight = FontWeight.Bold, color = CoffeeDark) },
        text = {
            Text(
                "En cumplimiento de la Ley 1581 de 2012 (Protección de Datos Personales en Colombia):\n\n" +
                        "1. Sus datos personales (nombre, correo) y agronómicos serán almacenados localmente en su dispositivo.\n" +
                        "2. Si adquiere la versión PRO, las copias de seguridad en la nube estarán encriptadas y no serán compartidas con terceros.\n" +
                        "3. Usted tiene derecho a conocer, actualizar, rectificar y solicitar la eliminación de sus datos en cualquier momento."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Entendido", color = CoffeeDark, fontWeight = FontWeight.Bold) }
        },
        containerColor = Color(0xFFFDF8F5)
    )
}