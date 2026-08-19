package com.soto.coffeelog_huila

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soto.coffeelog_huila.data.AppDatabase
import com.soto.coffeelog_huila.data.SessionManager
import com.soto.coffeelog_huila.ui.Screens
import com.soto.coffeelog_huila.ui.auth.AuthViewModel
import com.soto.coffeelog_huila.ui.auth.LoginScreen
import com.soto.coffeelog_huila.ui.auth.RegisterScreen
import com.soto.coffeelog_huila.ui.auth.RoleSelectionScreen
import com.soto.coffeelog_huila.ui.onboarding.OnboardingScreen
import com.soto.coffeelog_huila.ui.onboarding.SplashScreen
import com.soto.coffeelog_huila.ui.theme.CoffeeLogHuilaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicializamos la base de datos y el gestor de sesión
        val database = AppDatabase.getDatabase(this)
        val dao = database.coffeeDao()
        val sessionManager = SessionManager(this)

        // 2. Inicializamos el ViewModel de Autenticación
        val authViewModel = AuthViewModel(dao, sessionManager)

        setContent {
            CoffeeLogHuilaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Llamamos al motor de navegación
                    CoffeeNavHost(authViewModel, sessionManager)
                }
            }
        }
    }
}

@Composable
fun CoffeeNavHost(authViewModel: AuthViewModel, sessionManager: SessionManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {

        // 1. Pantalla de Carga
        composable(Screens.Splash.route) {
            SplashScreen(navController = navController, sessionManager = sessionManager)
        }

        // 2. Carrusel de Bienvenida
        composable(Screens.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        // 3. Pantalla de Login
        composable(Screens.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screens.Register.route) }
            )
        }

        // 4. Pantalla de Registro
        composable(Screens.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToRole = { navController.navigate(Screens.RoleSelection.route) },
                onNavigateToLogin = { navController.navigateUp() } // Se devuelve al Login
            )
        }

        // 5. Pantalla de Selección de Rol
        composable(Screens.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { rolSeleccionado ->
                    authViewModel.registrarConRol(rolSeleccionado)
                    when(rolSeleccionado.name) {
                        "PRODUCTOR" -> navController.navigate(Screens.HomeProductor.route)
                        "CATADOR" -> navController.navigate(Screens.HomeCatador.route)
                        else -> navController.navigate(Screens.HomeAdmin.route)
                    }
                }
            )
        }

        // 6. Dashboards según Rol
        composable(Screens.HomeProductor.route) {
            Text(text = "Bienvenido Productor - Dashboard de Fincas")
        }
        composable(Screens.HomeCatador.route) {
            Text(text = "Bienvenido Catador - Módulo SCA")
        }
        composable(Screens.HomeAdmin.route) {
            Text(text = "Bienvenido Administrador - Gestión total")
        }
    }
}