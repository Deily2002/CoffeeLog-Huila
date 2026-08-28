package com.soto.coffeelog_huila.ui.cataciones

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soto.coffeelog_huila.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CatacionViewModel(private val dao: CoffeeDao, private val session: SessionManager) : ViewModel() {

    // Lista de lotes para el menú desplegable
    val lotesDisponibles = dao.obtenerTodosLosLotesActivos()

    // Control del Stepper (0: Info, 1: Atributos, 2: Notas)
    var pasoActual by mutableIntStateOf(0)

    // DATOS DE INFORMACIÓN
    var loteSeleccionado by mutableStateOf<LoteEntity?>(null)
    var catadorNombre by mutableStateOf("")
    var fechaCatacion by mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    var nivelTueste by mutableStateOf("Medio")

    // ATRIBUTOS SCA (Inician en los valores estándar de la SCA)
    var fragancia by mutableFloatStateOf(8.0f)
    var sabor by mutableFloatStateOf(8.0f)
    var saborResidual by mutableFloatStateOf(8.0f)
    var acidez by mutableFloatStateOf(8.0f)
    var cuerpo by mutableFloatStateOf(8.0f)
    var balance by mutableFloatStateOf(8.0f)
    var uniformidad by mutableFloatStateOf(10.0f)
    var tazaLimpia by mutableFloatStateOf(10.0f)
    var dulzor by mutableFloatStateOf(10.0f)
    var puntajeCatador by mutableFloatStateOf(8.0f)

    var notas by mutableStateOf("")

    // MATEMÁTICA EN TIEMPO REAL
    val puntajeTotal: Float
        get() = fragancia + sabor + saborResidual + acidez + cuerpo + balance + uniformidad + tazaLimpia + dulzor + puntajeCatador

    val calidadEvaluada: CalidadSCA
        get() = when {
            puntajeTotal >= 90f -> CalidadSCA.EXCELENTE
            puntajeTotal >= 85f -> CalidadSCA.MUY_BUENA
            puntajeTotal >= 80f -> CalidadSCA.BUENA
            else -> CalidadSCA.REGULAR
        }

    // FUNCIÓN PARA GUARDAR EN LA BASE DE DATOS
    fun guardarCatacion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loteSeleccionado?.let { lote ->
                val nuevaCatacion = CatacionEntity(
                    loteId = lote.id,
                    usuarioId = session.getUserId(),
                    fechaCatacion = fechaCatacion,
                    nivelTueste = nivelTueste,
                    fraganciaAroma = fragancia,
                    sabor = sabor,
                    saborResidual = saborResidual,
                    acidez = acidez,
                    cuerpo = cuerpo,
                    balance = balance,
                    uniformidad = uniformidad,
                    tazaLimpia = tazaLimpia,
                    dulzor = dulzor,
                    puntajeCatador = puntajeCatador,
                    puntajeTotal = puntajeTotal,
                    calidadSugerida = calidadEvaluada,
                    notasCatador = notas
                )
                dao.insertarCatacion(nuevaCatacion)
                onSuccess()
            }
        }
    }
}