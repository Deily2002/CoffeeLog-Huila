package com.soto.coffeelog_huila.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.data.SessionManager
import com.soto.coffeelog_huila.ui.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, sessionManager: SessionManager) {

    // Lógica del tiempo (3 segundos)
    LaunchedEffect(key1 = true) {
        delay(3000)
        if (sessionManager.isLogged()) {
            val ruta = when (sessionManager.getRol()) {
                "PRODUCTOR" -> Screens.HomeProductor.route
                "CATADOR" -> Screens.HomeCatador.route
                else -> Screens.HomeAdmin.route
            }
            navController.navigate(ruta) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screens.Onboarding.route) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            }
        }
    }

    // EL DISEÑO VISUAL
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de Fondo
        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = "Fondo Splash",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.logo_coffeelog),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Trazabilidad y perfil sensorial\nde cafés de especialidad",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Cada grano cuenta",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "una historia",
                color = Color(0xFFFFB300),
                fontSize = 38.sp,
                fontFamily = FontFamily.Cursive,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(30.dp))

            // BARRITA DE CARGA HORIZONTAL
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}