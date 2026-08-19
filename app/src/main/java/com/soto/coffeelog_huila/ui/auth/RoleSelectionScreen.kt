package com.soto.coffeelog_huila.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.data.RolUsuario
import com.soto.coffeelog_huila.ui.theme.CoffeeDark

@Composable
fun RoleSelectionScreen(onRoleSelected: (RolUsuario) -> Unit) {
    var rolSeleccionado by remember { mutableStateOf<RolUsuario?>(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFDF8F5))
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium, color = CoffeeDark, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            // IMAGEN CIRCULAR DEL GRANJERO
            Image(
                painter = painterResource(id = R.drawable.img_granjero),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("¿Qué rol tienes?", style = MaterialTheme.typography.titleLarge, color = CoffeeDark, fontWeight = FontWeight.Bold)

            Text(
                "Selecciona la opción que mejor te\nrepresenta en el mundo del café.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // 1. TARJETA PRODUCTOR
            RoleCard(
                title = "Productor / Finca",
                subtitle = "Registro y gestión de mis lotes\ny cataciones",
                iconResId = R.drawable.ic_productor, // Tu nuevo ícono
                isSelected = rolSeleccionado == RolUsuario.PRODUCTOR,
                onClick = { rolSeleccionado = RolUsuario.PRODUCTOR }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. TARJETA CATADOR
            RoleCard(
                title = "Catador",
                subtitle = "Realizo cataciones y\nevaluaciones de calidad",
                iconResId = R.drawable.ic_catador, // Tu nuevo ícono
                isSelected = rolSeleccionado == RolUsuario.CATADOR,
                onClick = { rolSeleccionado = RolUsuario.CATADOR }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. TARJETA ADMINISTRADOR
            RoleCard(
                title = "Administrador",
                subtitle = "Gestiono equipos y usuarios",
                iconResId = R.drawable.ic_admin,
                isSelected = rolSeleccionado == RolUsuario.ADMIN,
                onClick = { rolSeleccionado = RolUsuario.ADMIN }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN CONTINUAR
            Button(
                onClick = { rolSeleccionado?.let { onRoleSelected(it) } },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoffeeDark,
                    disabledContainerColor = Color.LightGray
                ),
                enabled = rolSeleccionado != null
            ) {
                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RoleCard(title: String, subtitle: String, iconResId: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) CoffeeDark else Color.White
    val textColor = if (isSelected) Color.White else CoffeeDark
    val subtitleColor = if (isSelected) Color(0xFFD7CCC8) else Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFEFEBE9)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor del ícono
            Box(
                modifier = Modifier
                    .size(40.dp)
            ) {
                // AQUÍ USAMOS TUS ÍCONOS Y LES APLICAMOS COLOR (TINT)
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                Text(subtitle, color = subtitleColor, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = textColor
            )
        }
    }
}