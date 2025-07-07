package com.example.vidasana.database

import androidx.room.*
import com.example.vidasana.models.Habito

@Dao
interface HabitoDao {
    @Insert
    suspend fun insertar(habito: Habito)

    @Query("SELECT * FROM habitos WHERE usuario_id = :usuarioId ORDER BY fecha DESC")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<Habito>


    @Delete
    suspend fun eliminar(habito: Habito)
}
