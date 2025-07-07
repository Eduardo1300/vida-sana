package com.example.vidasana.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habitos")
data class Habito(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,
    val cantidad: String,
    val fecha: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int
)
