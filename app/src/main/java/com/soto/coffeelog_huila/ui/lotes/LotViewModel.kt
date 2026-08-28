package com.soto.coffeelog_huila.ui.lotes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soto.coffeelog_huila.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LotViewModel(private val dao: CoffeeDao, private val sessionManager: SessionManager) : ViewModel() {

    // ============================================================================
    // 1. ESTADOS DE LA UI (Variables que actualizan la pantalla)
    // ============================================================================

    // Trae los lotes en tiempo real desde la Base de Datos
    val lotes = dao.obtenerLotesPorUsuario(sessionManager.getUserId()).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Mensaje flotante (Snackbar)
    var snackbarMessage by mutableStateOf<String?>(null)
        private set

    fun mostrarMensaje(mensaje: String) {
        viewModelScope.launch {
            snackbarMessage = mensaje
            delay(4000)
            snackbarMessage = null
        }
    }


    // ============================================================================
    // 2. CONSULTAS DE LECTURA (Traer datos específicos)
    // ============================================================================

    suspend fun obtenerLote(id: Long): LoteEntity? {
        return dao.obtenerLotePorId(id)
    }

    suspend fun obtenerFinca(id: Long): FincaEntity? {
        return dao.obtenerFincaPorId(id)
    }

    fun obtenerCatacionesDeLote(loteId: Long): Flow<List<CatacionEntity>> {
        return dao.obtenerCatacionesPorLote(loteId)
    }

    suspend fun obtenerNombreCatador(id: Long): String {
        return dao.obtenerNombreUsuario(id) ?: "Catador Independiente"
    }


    // ============================================================================
    // 3. OPERACIONES DE ESCRITURA (Guardar, Actualizar, Borrar)
    // ============================================================================

    fun eliminarLote(lote: LoteEntity) {
        viewModelScope.launch {
            dao.eliminarLote(lote)
            mostrarMensaje("Se eliminó el ${lote.numeroLote} correctamente")
        }
    }

    fun guardarLoteCompleto(
        loteId: Long? = null,
        nombreFinca: String, numeroLote: String, variedad: String,
        proceso: ProcesoCafe, fecha: Long, altitud: String,
        edadArboles: String, produccion: String, factor: String,
        humedad: String, pesoTotal: String, notas: String,
        usuarioId: Long, imagenUri: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            // Revisa si la finca ya existe. Si no, la crea.
            val fincaExistente = dao.obtenerFincaPorNombreYUsuario(nombreFinca.trim(), usuarioId)
            val idFincaReal = fincaExistente?.id ?: dao.insertarFinca(
                FincaEntity(usuarioId = usuarioId, nombre = nombreFinca.trim(), municipio = "Huila")
            )

            // Empaquetando la información
            val lote = LoteEntity(
                id = loteId ?: 0L,
                fincaId = idFincaReal, numeroLote = numeroLote.trim(), variedad = variedad.trim(),
                proceso = proceso, fechaCosecha = fecha, altitud = altitud.toIntOrNull(),
                edadArboles = edadArboles.toIntOrNull(), produccionAnual = produccion.toFloatOrNull(),
                factorRendimiento = factor.toFloatOrNull(), humedad = humedad.toFloatOrNull(),
                pesoTotal = pesoTotal.toFloatOrNull() ?: 0f, notasAdicionales = notas.trim().ifEmpty { null },
                imagenUri = imagenUri, estado = EstadoLote.ACTIVO
            )

            if (loteId != null && loteId > 0) {
                dao.actualizarLote(lote)
                mostrarMensaje("${lote.numeroLote} actualizado correctamente")
            } else {
                dao.insertarLote(lote)
                mostrarMensaje("${lote.numeroLote} guardado correctamente")
            }

            onSuccess()
        }
    }
}