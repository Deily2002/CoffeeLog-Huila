package com.soto.coffeelog_huila.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        UsuarioEntity::class, FincaEntity::class, LoteEntity::class,
        CatacionEntity::class, ClienteEntity::class, VentaEntity::class,
        FotoEntity::class, SyncLogEntity::class, ConfigEntity::class, VariedadEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coffeeDao(): CoffeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffeelog_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}