package com.soto.coffeelog_huila.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Este efecto está "escuchando". Si el ViewModel dice que es un usuario nuevo de Google, cambia de pantalla.
    LaunchedEffect(viewModel.navigateToRoleSelection) {
        if (viewModel.navigateToRoleSelection) {
            onNavigateToRegister() // Reutilizamos la navegación para ir al flujo de registro/rol
            viewModel.navigateToRoleSelection = false // Apagamos el aviso
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFDF8F5))
    ) {

        // DECORACIÓN FONDO
        Image(
            painter = painterResource(id = R.drawable.decoracion_cafe),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        // 2. Maneja los espacios del teclado y el sistema
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_coffeelog),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium, color = CoffeeDark, fontWeight = FontWeight.Bold)
            Text("Accede a tu cuenta", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // CONFIGURACIÓN DE COLORES PARA LOS CAMPOS
            val fieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = CoffeeDark
            )

            // CAMPO CORREO
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico", color = Color.Gray) },
                leadingIcon = { Icon(painterResource(id = R.drawable.ic_email), contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(5.dp))

            // CAMPO CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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

            TextButton(onClick = { /* Lógica de recuperación */ }, modifier = Modifier.align(Alignment.End)) {
                Text("¿Olvidaste tu contraseña?", color = CoffeeDark, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)
            ) {
                Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
                Text(
                    text = "o continúa con",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN GOOGLE
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val credentialManager = androidx.credentials.CredentialManager.create(context)

                            val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("1023874576900-eaaf9f159hfq0j01pnbno954mq92sqrk.apps.googleusercontent.com")
                                .build()

                            val request = androidx.credentials.GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            val credential = result.credential

                            if (credential is androidx.credentials.CustomCredential &&
                                credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                                val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)

                                val email = googleIdTokenCredential.id
                                val name = googleIdTokenCredential.displayName ?: "Usuario Google"

                                viewModel.procesarGoogleLogin(email, name)
                            }
                        } catch (e: Exception) {
                            viewModel.errorMessage = "Error al conectar con Google. Verifica tu conexión."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(id = R.drawable.ic_google), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continuar con Google", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text("¿No tienes cuenta? ", color = Color.Gray)
                Text("Regístrate", color = CoffeeDark, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateToRegister() })
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}