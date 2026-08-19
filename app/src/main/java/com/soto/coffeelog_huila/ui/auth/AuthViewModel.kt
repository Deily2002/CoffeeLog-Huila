package com.soto.coffeelog_huila.ui.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soto.coffeelog_huila.data.*
import kotlinx.coroutines.launch

class AuthViewModel(private val dao: CoffeeDao, private val session: SessionManager) : ViewModel() {

    var loginStatus by mutableStateOf<RolUsuario?>(null)
    var errorMessage by mutableStateOf("")

    fun login(correo: String, pass: String) {
        viewModelScope.launch {
            val user = dao.login(correo, pass)
            if (user != null) {
                session.saveUser(user.id, user.rol.name)
                loginStatus = user.rol
            } else {
                errorMessage = "Datos incorrectos"
            }
        }
    }

    // Variables temporales para el registro
    var regNombre by mutableStateOf("")
    var regCorreo by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regTelefono by mutableStateOf("")

    // Función que se llamará cuando el usuario haga clic en su ROL
    fun registrarConRol(rol: RolUsuario) {
        viewModelScope.launch {
            val user = UsuarioEntity(
                nombre = regNombre,
                correo = regCorreo,
                password = regPassword,
                telefono = regTelefono.ifEmpty { null },
                rol = rol
            )
            val id = dao.registrarUsuario(user)
            session.saveUser(id, rol.name)
            loginStatus = rol
        }
    }

    // Variable para avisarle a la pantalla que debe ir a Selección de Rol
    var navigateToRoleSelection by mutableStateOf(false)

    // Función que procesa el inicio de sesión con Google
    fun procesarGoogleLogin(correo: String, nombre: String) {
        viewModelScope.launch {
            val user = dao.buscarUsuarioPorCorreo(correo)
            if (user != null) {
                // Si el usuario ya existe en la Base de Datos, lo dejamos entrar a su Dashboard
                session.saveUser(user.id, user.rol.name)
                loginStatus = user.rol
            } else {
                // Si es un usuario NUEVO, guardamos sus datos temporalmente
                regNombre = nombre
                regCorreo = correo
                regPassword = "google_sso_password" // Contraseña interna por defecto

                // Le avisamos a la pantalla de Login que lo mande a elegir su rol
                navigateToRoleSelection = true
            }
        }
    }
}

