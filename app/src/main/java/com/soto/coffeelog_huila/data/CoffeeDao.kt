package com.soto.coffeelog_huila.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {

    // ============================================================================
    // 1. USUARIOS
    // ============================================================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: UsuarioEntity): Long

    // Login a prueba de errores del teclado (ignora mayúsculas/minúsculas en el correo)
    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) AND password = :pass LIMIT 1")
    suspend fun login(correo: String, pass: String): UsuarioEntity?

    // Busca si el usuario de Google ya está registrado
    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarUsuarioPorCorreo(correo: String): UsuarioEntity?

    // Busca el nombre de un usuario usando su ID
    @Query("SELECT nombre FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerNombreUsuario(id: Long): String?


    // ============================================================================
    // 2. FINCAS
    // ============================================================================

    // Devuelve un Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFinca(finca: FincaEntity): Long

    // Busca si la finca ya existe para no crear duplicados al guardar un lote
    @Query("SELECT * FROM fincas WHERE nombre = :nombre AND usuarioId = :userId LIMIT 1")
    suspend fun obtenerFincaPorNombreYUsuario(nombre: String, userId: Long): FincaEntity?

    // Obtiene los datos de una finca específica
    @Query("SELECT * FROM fincas WHERE id = :fincaId LIMIT 1")
    suspend fun obtenerFincaPorId(fincaId: Long): FincaEntity?

    // Lista todas las fincas de un usuario
    @Query("SELECT * FROM fincas WHERE usuarioId = :usuarioId")
    fun obtenerFincasPorUsuario(usuarioId: Long): Flow<List<FincaEntity>>


    // ============================================================================
    // 3. LOTES
    // ============================================================================

    @Insert
    suspend fun insertarLote(lote: LoteEntity)

    @Update
    suspend fun actualizarLote(lote: LoteEntity)

    @Delete
    suspend fun eliminarLote(lote: LoteEntity)

    // Detalle de un lote específico
    @Query("SELECT * FROM lotes WHERE id = :loteId LIMIT 1")
    suspend fun obtenerLotePorId(loteId: Long): LoteEntity?

    // Lista todos los lotes de una finca
    @Query("SELECT * FROM lotes WHERE fincaId = :fincaId")
    fun obtenerLotesPorFinca(fincaId: Long): Flow<List<LoteEntity>>

    // Lista todos los lotes de un usuario (Cruzando las tablas de lotes y fincas)
    @Query("SELECT lotes.* FROM lotes INNER JOIN fincas ON lotes.fincaId = fincas.id WHERE fincas.usuarioId = :usuarioId ORDER BY lotes.id DESC")
    fun obtenerLotesPorUsuario(usuarioId: Long): Flow<List<LoteEntity>>

    // Lista los lotes activos (Para el menú desplegable de Nueva Catación)
    @Query("SELECT * FROM lotes WHERE estado = 'ACTIVO'")
    fun obtenerTodosLosLotesActivos(): Flow<List<LoteEntity>>


    // ============================================================================
    // 4. CATACIONES
    // ============================================================================

    @Insert
    suspend fun insertarCatacion(catacion: CatacionEntity)

    // Lista el historial de cataciones de un lote específico
    @Query("SELECT * FROM cataciones WHERE loteId = :loteId ORDER BY fechaCatacion DESC")
    fun obtenerCatacionesPorLote(loteId: Long): Flow<List<CatacionEntity>>


    // ============================================================================
    // 5. ESTADÍSTICAS DEL DASHBOARD
    // ============================================================================

    // Cuenta cuántos lotes tiene este usuario (buscando a través de sus fincas)
    @Query("SELECT COUNT(lotes.id) FROM lotes INNER JOIN fincas ON lotes.fincaId = fincas.id WHERE fincas.usuarioId = :userId")
    fun contarLotesPorUsuario(userId: Long): Flow<Int>

    // Cuenta cuántas cataciones ha hecho este usuario
    @Query("SELECT COUNT(*) FROM cataciones WHERE usuarioId = :userId")
    fun contarCatacionesPorUsuario(userId: Long): Flow<Int>

    // Saca el promedio matemático exacto de todas sus cataciones
    @Query("SELECT AVG(puntajeTotal) FROM cataciones WHERE usuarioId = :userId")
    fun promedioPuntajePorUsuario(userId: Long): Flow<Float?>

}