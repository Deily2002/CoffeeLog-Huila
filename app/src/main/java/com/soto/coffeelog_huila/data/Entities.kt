package com.soto.coffeelog_huila.data

import androidx.room.*

// 1. USUARIOS (Login y Perfil)
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    val rol: RolUsuario,
    val preguntaSeguridad: String? = null,
    val respuestaSeguridad: String? = null,
    val fotoPerfil: String? = null
)

// 2. FINCAS (Ubicación, altitud y producción)
@Entity(
    tableName = "fincas",
    foreignKeys = [ForeignKey(entity = UsuarioEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)]
)
data class FincaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val usuarioId: Long,
    val nombre: String,
    val municipio: String,
    val departamento: String = "Huila",
    val altitud: Int? = null,
    val produccionAnualEstimada: Float? = null
)

// 3. LOTES (Trazabilidad y Agronomía)
@Entity(
    tableName = "lotes",
    foreignKeys = [ForeignKey(entity = FincaEntity::class, parentColumns = ["id"], childColumns = ["fincaId"], onDelete = ForeignKey.CASCADE)]
)
data class LoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val fincaId: Long,
    val numeroLote: String,
    val variedad: String,
    val proceso: ProcesoCafe,
    val fechaCosecha: Long,
    val pesoTotal: Float,
    val edadArboles: Int? = null,
    val factorRendimiento: Float? = null,
    val estado: EstadoLote = EstadoLote.ACTIVO
)

// 4. CATACIONES (SCA y Radar Chart)
@Entity(
    tableName = "cataciones",
    foreignKeys = [
        ForeignKey(entity = LoteEntity::class, parentColumns = ["id"], childColumns = ["loteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = UsuarioEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CatacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val loteId: Long,
    @ColumnInfo(index = true) val usuarioId: Long,
    val fecha: Long = System.currentTimeMillis(),
    val fraganciaAroma: Float,
    val sabor: Float,
    val saborResidual: Float,
    val acidez: Float,
    val cuerpo: Float,
    val balance: Float,
    val uniformidad: Float,
    val tazaLimpia: Float,
    val dulzor: Float,
    val puntajeCatador: Float,
    val puntajeTotal: Float,
    val notas: String?
)

// 5. CLIENTES (CRM básico)
@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empresa: String,
    val contacto: String,
    val telefono: String?
)

// 6. VENTAS (Impacto financiero)
@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(entity = LoteEntity::class, parentColumns = ["id"], childColumns = ["loteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ClienteEntity::class, parentColumns = ["id"], childColumns = ["clienteId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class VentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val loteId: Long,
    @ColumnInfo(index = true) val clienteId: Long,
    val total: Double,
    val fecha: Long
)

// 7. FOTOS (Galería multimedia)
@Entity(
    tableName = "fotos",
    foreignKeys = [ForeignKey(entity = LoteEntity::class, parentColumns = ["id"], childColumns = ["loteId"], onDelete = ForeignKey.CASCADE)]
)
data class FotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val loteId: Long,
    val ruta: String
)

// 8. LOGS DE SINCRONIZACIÓN (Control Offline)
@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: Long,
    val estado: EstadoSync
)

// 9. CONFIGURACIÓN (Ajustes de la app)
@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey val id: Int = 1,
    val unidadMedida: String = "Kg",
    val moneda: String = "COP"
)

// 10. VARIEDADES (Maestro de datos)
@Entity(tableName = "variedades")
data class VariedadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)