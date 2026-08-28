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
import com.soto.coffeelog_huila.data.CoffeeDao
import com.soto.coffeelog_huila.data.RolUsuario
import com.soto.coffeelog_huila.data.SessionManager
import com.soto.coffeelog_huila.ui.Screens
import com.soto.coffeelog_huila.ui.auth.AuthViewModel
import com.soto.coffeelog_huila.ui.auth.LoginScreen
import com.soto.coffeelog_huila.ui.auth.RegisterScreen
import com.soto.coffeelog_huila.ui.auth.RoleSelectionScreen
import com.soto.coffeelog_huila.ui.lotes.LotViewModel
import com.soto.coffeelog_huila.ui.lotes.LotesListScreen
import com.soto.coffeelog_huila.ui.lotes.NuevoLoteScreen
import com.soto.coffeelog_huila.ui.onboarding.OnboardingScreen
import com.soto.coffeelog_huila.ui.onboarding.SplashScreen
import com.soto.coffeelog_huila.ui.theme.CoffeeLogHuilaTheme
import com.soto.coffeelog_huila.ui.cataciones.CatacionViewModel
import com.soto.coffeelog_huila.ui.cataciones.NuevaCatacionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicializamos la base de datos y el gestor de sesión
        val database = AppDatabase.getDatabase(this)
        val dao = database.coffeeDao()
        val sessionManager = SessionManager(this)

        // 2. Inicializamos el ViewModel de Autenticación (Recuperando el nombre si ya hay sesión)
        val authViewModel = AuthViewModel(dao, sessionManager).apply {
            if (sessionManager.isLogged()) {
                userName = sessionManager.getNombre()
            }
        }

        setContent {
            CoffeeLogHuilaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Llamamos al motor de navegación y le pasamos el DAO
                    CoffeeNavHost(authViewModel, sessionManager, dao)
                }
            }
        }
    }
}

@Composable
fun CoffeeNavHost(authViewModel: AuthViewModel, sessionManager: SessionManager, dao: CoffeeDao) {
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
                onNavigateToRegister = { navController.navigate(Screens.Register.route) },
                onLoginSuccess = { rolLogueado ->
                    val rutaDestino = when(rolLogueado) {
                        RolUsuario.PRODUCTOR -> Screens.HomeProductor.route
                        RolUsuario.CATADOR -> Screens.HomeCatador.route
                        RolUsuario.ADMIN -> Screens.HomeAdmin.route
                    }
                    navController.navigate(rutaDestino) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Pantalla de Registro
        composable(Screens.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToRole = { navController.navigate(Screens.RoleSelection.route) },
                onNavigateToLogin = { navController.navigateUp() }
            )
        }

        // 5. Pantalla de Selección de Rol
        composable(Screens.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { rolSeleccionado ->
                    authViewModel.registrarConRol(rolSeleccionado)

                    val rutaDestino = when(rolSeleccionado) {
                        RolUsuario.PRODUCTOR -> Screens.HomeProductor.route
                        RolUsuario.CATADOR -> Screens.HomeCatador.route
                        RolUsuario.ADMIN -> Screens.HomeAdmin.route
                    }
                    navController.navigate(rutaDestino) {
                        popUpTo(Screens.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // 6. Dashboards según Rol
        composable(Screens.HomeProductor.route) {
            com.soto.coffeelog_huila.ui.home.HomeProductorScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // 7. Lista de Lotes
        composable("lotes_list") {
            val lotViewModel = androidx.lifecycle.viewmodel.compose.viewModel { LotViewModel(dao, sessionManager) }
            LotesListScreen(navController = navController, viewModel = lotViewModel)
        }

        // 8. Formulario de Nuevo Lote
        composable("nuevo_lote") {
            val lotViewModel = androidx.lifecycle.viewmodel.compose.viewModel { LotViewModel(dao, sessionManager) }
            NuevoLoteScreen(navController = navController, viewModel = lotViewModel)
        }

        // 9. Editar Lote
        composable("editar_lote/{loteId}") { backStackEntry ->
            val loteId = backStackEntry.arguments?.getString("loteId")?.toLongOrNull()
            val lotViewModel = androidx.lifecycle.viewmodel.compose.viewModel { LotViewModel(dao, sessionManager) }
            NuevoLoteScreen(navController = navController, viewModel = lotViewModel, loteIdParaEditar = loteId)
        }

        // 10. Pantalla de Detalle
        composable("detalle_lote/{loteId}") { backStackEntry ->
            val loteId = backStackEntry.arguments?.getString("loteId")?.toLongOrNull() ?: 0L
            val lotViewModel = androidx.lifecycle.viewmodel.compose.viewModel { LotViewModel(dao, sessionManager) }
            com.soto.coffeelog_huila.ui.lotes.DetalleLoteScreen(navController = navController, viewModel = lotViewModel, loteId = loteId)
        }

        // 11. NUEVA CATACIÓN
        composable("nueva_catacion") {
            val catacionViewModel = androidx.lifecycle.viewmodel.compose.viewModel { CatacionViewModel(dao, sessionManager) }
            NuevaCatacionScreen(navController = navController, viewModel = catacionViewModel)
        }

        composable(Screens.HomeCatador.route) {
            Text(text = "Bienvenido Catador - Módulo SCA")
        }

        composable(Screens.HomeAdmin.route) {
            Text(text = "Bienvenido Administrador - Gestión total")
        }
    }
}