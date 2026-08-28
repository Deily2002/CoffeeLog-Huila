package com.soto.coffeelog_huila.ui.home

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar
import kotlinx.coroutines.launch
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import com.soto.coffeelog_huila.ui.theme.AccentGreen
import com.soto.coffeelog_huila.ui.auth.AuthViewModel
import com.soto.coffeelog_huila.ui.Screens
import com.soto.coffeelog_huila.data.SessionManager

@Composable
fun HomeProductorScreen(navController: NavController, viewModel: AuthViewModel) {

    // 1. VARIABLES DE ESTADO Y NAVEGACIÓN
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // 2. LÓGICA DE TEXTOS
    val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val saludo = when (horaActual) {
        in 5..11 -> "Buenos días"
        in 12..18 -> "Buenas tardes"
        else -> "Buenas noches"
    }

    // Obtenemos el nombre de la sesión. Si falla, pone "Productor" por defecto.
    val nombreCompleto = sessionManager.getNombre().ifEmpty { "Productor" }
    val primerNombre = nombreCompleto.trim().split(" ").first()

    // 3. TRAER ESTADÍSTICAS EN TIEMPO REAL DESDE LA BASE DE DATOS
    val totalLotes by viewModel.totalLotes.collectAsState(initial = 0)
    val totalCataciones by viewModel.totalCataciones.collectAsState(initial = 0)
    val promedio by viewModel.puntajePromedio.collectAsState(initial = null)

    // Lógica para formatear el número
    val promedioReal = promedio ?: 0f
    val promedioFormateado = if (promedioReal == 0f) "0,0 pts" else String.format("%.1f", promedioReal) + " pts"

    // Lógica para la Etiqueta de Calidad y su Color
    val (calidadTexto, calidadColor) = when {
        promedioReal == 0f -> Pair("Sin catar", Color.Gray)
        promedioReal >= 90f -> Pair("Excelente", Color(0xFF2E7D32))
        promedioReal >= 85f -> Pair("Muy buena", AccentGreen)
        promedioReal >= 80f -> Pair("Buena", Color(0xFFE65100))
        else -> Pair("Regular", Color.Red)
    }

    // 4. EL COMPONENTE DEL MENÚ LATERAL (DRAWER)
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xE61E1E1E),
                modifier = Modifier.width(300.dp)
            ) {
                // CONTENIDO DEL MENÚ LATERAL
                DrawerMenuContent(
                    nombre = primerNombre,
                    onCerrarSesion = {
                        coroutineScope.launch {
                            drawerState.close()
                            sessionManager.logout()
                            navController.navigate(Screens.Login.route) {
                                popUpTo(0)
                            }
                        }
                    }
                )
            }
        }
    ) {
        // 5. ESTRUCTURA VISUAL PRINCIPAL
        Box(modifier = Modifier.fillMaxSize()) {

            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.bg_splash),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

            Scaffold(
                bottomBar = { CustomBottomNavigation(navController, currentScreen = "inicio") },
                containerColor = Color.Transparent
            ) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                        .systemBarsPadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // --- ENCABEZADO ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // BOTÓN PARA ABRIR EL MENÚ LATERAL
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text("$saludo, $primerNombre 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Box {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = Color.White, modifier = Modifier.size(28.dp))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Red).align(Alignment.TopEnd))
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // --- TARJETA 1: RESUMEN GENERAL ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8F5)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Resumen general", fontWeight = FontWeight.Bold, color = CoffeeDark)
                                Text("Histórico ⌄", color = Color.Gray, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                StatItem(title = "Lotes", value = totalLotes.toString(), color = Color(0xFFD84315), modifier = Modifier.weight(1f))
                                StatItem(title = "Cataciones", value = totalCataciones.toString(), color = Color(0xFFD84315), modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                StatItem(title = "Puntaje promedio", value = promedioFormateado, color = calidadColor, modifier = Modifier.weight(1f))
                                StatItem(title = "Calidad promedio", value = calidadTexto, color = calidadColor, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- Tarjeta 2: ACCESOS RÁPIDOS ---
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8F5))) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Accesos rápidos", fontWeight = FontWeight.Bold, color = CoffeeDark)
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                QuickAccessIcon(
                                    icon = Icons.Outlined.Eco,
                                    label = "Lotes",
                                    onClick = { navController.navigate("lotes_list") }
                                )
                                QuickAccessIcon(
                                    icon = Icons.Outlined.Coffee,
                                    label = "Cataciones",
                                    onClick = { navController.navigate("nueva_catacion") }
                                )
                                QuickAccessIcon(icon = Icons.Outlined.LocalCafe, label = "Perfiles")
                                QuickAccessIcon(icon = Icons.Outlined.Assessment, label = "Reportes")
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                QuickAccessIcon(icon = Icons.Outlined.Handshake, label = "Clientes")
                                QuickAccessIcon(icon = Icons.Outlined.IosShare, label = "Exportar")
                                QuickAccessIcon(icon = Icons.Outlined.TrendingUp, label = "Impacto")
                                QuickAccessIcon(icon = Icons.Outlined.Settings, label = "Ajustes")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- Tarjeta 3: NUEVA CATACIÓN ---
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("nueva_catacion") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFD84315), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nueva catación", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 16.sp)
                                Text("Registra una nueva evaluación\nde tu café", color = CoffeeDark, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(CoffeeDark), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// ==========================================
// COMPONENTES AUXILIARES DEL MENÚ LATERAL
// ==========================================

@Composable
fun DrawerMenuContent(nombre: String, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).systemBarsPadding()
    ) {
        // PERFIL
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.img_granjero),
                contentDescription = "Perfil",
                modifier = Modifier.size(60.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(nombre, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Productor", color = Color.LightGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // TARJETA PREMIUM
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
            border = BorderStroke(1.dp, Color(0xFFFFB300)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Plan Premium", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Activo hasta 12/06/2027", color = Color(0xFFFFB300), fontSize = 12.sp)
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFFFB300))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // OPCIONES DEL MENÚ
        DrawerItem(icon = Icons.Outlined.Home, title = "Mi finca")
        DrawerItem(icon = Icons.Outlined.Person, title = "Perfil de cuenta")
        DrawerItem(icon = Icons.Outlined.Sync, title = "Sincronizar (Wi-Fi)", subtitle = "Última: Hoy 08:30 AM")
        DrawerItem(icon = Icons.Outlined.HelpOutline, title = "Ayuda y soporte")
        DrawerItem(icon = Icons.Outlined.Settings, title = "Configuración")

        Spacer(modifier = Modifier.weight(1f))

        // BOTÓN CERRAR SESIÓN
        DrawerItem(icon = Icons.Outlined.Logout, title = "Cerrar sesión", onClick = onCerrarSesion)

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DrawerItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

// ==========================================
// COMPONENTES AUXILIARES DEL DASHBOARD
// ==========================================

@Composable
fun StatItem(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, color = CoffeeDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QuickAccessIcon(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(55.dp).clip(CircleShape).background(Color.White).border(1.dp, Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CoffeeDark, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = CoffeeDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// MODIFICADO PARA RECIBIR EL NAVCONTROLLER
@Composable
fun CustomBottomNavigation(navController: NavController, currentScreen: String = "inicio") {
    NavigationBar(
        containerColor = Color(0x60000000), // Cristal Oscuro
        contentColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            selected = currentScreen == "inicio",
            onClick = {
                if (currentScreen != "inicio") {
                    navController.navigate(com.soto.coffeelog_huila.ui.Screens.HomeProductor.route) {
                        popUpTo(0)
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = CoffeeDark,
                unselectedIconColor = Color.LightGray, unselectedTextColor = Color.LightGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Eco, contentDescription = "Lotes") },
            label = { Text("Lotes", fontSize = 10.sp) },
            selected = currentScreen == "lotes",
            onClick = {
                if(currentScreen != "lotes") navController.navigate("lotes_list")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = CoffeeDark,
                unselectedIconColor = Color.LightGray, unselectedTextColor = Color.LightGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Coffee, contentDescription = "Cataciones") },
            label = { Text("Cataciones", fontSize = 10.sp) },
            selected = currentScreen == "nueva_catacion",
            onClick = {
                if(currentScreen != "nueva_catacion") navController.navigate("nueva_catacion")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = CoffeeDark,
                unselectedIconColor = Color.LightGray, unselectedTextColor = Color.LightGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.LocalCafe, contentDescription = "Perfiles") },
            label = { Text("Perfiles", fontSize = 10.sp) },
            selected = currentScreen == "perfiles",
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = CoffeeDark,
                unselectedIconColor = Color.LightGray, unselectedTextColor = Color.LightGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Más") },
            label = { Text("Más", fontSize = 10.sp) },
            selected = currentScreen == "mas",
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = CoffeeDark,
                unselectedIconColor = Color.LightGray, unselectedTextColor = Color.LightGray
            )
        )
    }
}