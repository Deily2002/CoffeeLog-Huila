package com.soto.coffeelog_huila.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    // USUARIOS: Para la pantalla de Registro y Login
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :pass LIMIT 1")
    suspend fun login(correo: String, pass: String): UsuarioEntity?

    // LOTES: Para la lista de lotes
    @Query("SELECT * FROM lotes WHERE fincaId = :fincaId")
    fun obtenerLotesPorFinca(fincaId: Long): Flow<List<LoteEntity>>

    @Insert
    suspend fun insertarLote(lote: LoteEntity)

    // CATACIONES: Para el Radar Chart
    @Insert
    suspend fun insertarCatacion(catacion: CatacionEntity)

    @Query("SELECT * FROM cataciones WHERE loteId = :loteId ORDER BY fecha DESC")
    fun obtenerCatacionesPorLote(loteId: Long): Flow<List<CatacionEntity>>
}