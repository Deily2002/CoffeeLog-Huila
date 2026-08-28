package com.soto.coffeelog_huila.ui.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soto.coffeelog_huila.data.*
import kotlinx.coroutines.launch

class AuthViewModel(private val dao: CoffeeDao, private val session: SessionManager) : ViewModel() {

    var loginStatus by mutableStateOf<RolUsuario?>(null)
    var errorMessage by mutableStateOf("")
    var userName by mutableStateOf(session.getNombre())
    var regNombre by mutableStateOf("")
    var regCorreo by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regTelefono by mutableStateOf("")
    var navigateToRoleSelection by mutableStateOf(false)

    val totalLotes = dao.contarLotesPorUsuario(session.getUserId())
    val totalCataciones = dao.contarCatacionesPorUsuario(session.getUserId())
    val puntajePromedio = dao.promedioPuntajePorUsuario(session.getUserId())

    fun login(correo: String, pass: String) {
        viewModelScope.launch {
            val user = dao.login(correo.trim(), pass.trim())
            if (user != null) {
                session.saveUser(user.id, user.rol.name, user.nombre)
                userName = user.nombre
                loginStatus = user.rol
            } else {
                errorMessage = "Correo o contraseña incorrectos"
            }
        }
    }

    fun registrarConRol(rol: RolUsuario) {
        viewModelScope.launch {
            val user = UsuarioEntity(
                nombre = regNombre.trim(),
                correo = regCorreo.trim(),
                password = regPassword.trim(),
                telefono = regTelefono.ifEmpty { null },
                rol = rol
            )
            val id = dao.registrarUsuario(user)
            session.saveUser(id, rol.name, regNombre.trim())
            userName = regNombre
            loginStatus = rol
        }
    }

    fun procesarGoogleLogin(correo: String, nombre: String) {
        viewModelScope.launch {
            val user = dao.buscarUsuarioPorCorreo(correo.trim())
            if (user != null) {
                session.saveUser(user.id, user.rol.name, user.nombre)
                userName = user.nombre
                loginStatus = user.rol
            } else {
                regNombre = nombre
                regCorreo = correo.trim()
                regPassword = "google_sso_password"
                navigateToRoleSelection = true
            }
        }
    }
}