package com.soto.coffeelog_huila.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter fun fromRol(value: RolUsuario) = value.name
    @TypeConverter fun toRol(value: String) = RolUsuario.valueOf(value)

    @TypeConverter fun fromProceso(value: ProcesoCafe) = value.name
    @TypeConverter fun toProceso(value: String) = ProcesoCafe.valueOf(value)

    @TypeConverter fun fromEstado(value: EstadoLote) = value.name
    @TypeConverter fun toEstado(value: String) = EstadoLote.valueOf(value)

    @TypeConverter fun fromSync(value: EstadoSync) = value.name
    @TypeConverter fun toSync(value: String) = EstadoSync.valueOf(value)

    @TypeConverter fun fromCalidad(value: CalidadSCA) = value.name
    @TypeConverter fun toCalidad(value: String) = CalidadSCA.valueOf(value)
}