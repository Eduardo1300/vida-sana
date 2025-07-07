package com.example.vidasana.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vidasana.R
import com.example.vidasana.database.HabitoDatabase
import kotlinx.coroutines.launch

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var tvEstadisticas: TextView
    private lateinit var barraAgua: ProgressBar
    private lateinit var barraComida: ProgressBar
    private lateinit var barraSueno: ProgressBar
    private lateinit var barraEmociones: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        tvEstadisticas = findViewById(R.id.tvEstadisticas)
        barraAgua = findViewById(R.id.progressAgua)
        barraComida = findViewById(R.id.progressComida)
        barraSueno = findViewById(R.id.progressSueno)
        barraEmociones = findViewById(R.id.progressEmociones)

        lifecycleScope.launch {
            val db = HabitoDatabase.getDatabase(this@EstadisticasActivity)
            val sharedPrefs = getSharedPreferences("session", MODE_PRIVATE)
            val usuarioId = sharedPrefs.getInt("usuario_id", -1)
            val lista = db.habitoDao().obtenerPorUsuario(usuarioId)

            val conteo = lista.groupingBy { it.tipo.lowercase() }.eachCount()

            val resumen = StringBuilder()
            conteo.forEach { (tipo, cantidad) ->
                resumen.append("${tipo.replaceFirstChar { it.uppercase() }}: $cantidad\n")
            }
            tvEstadisticas.text = resumen.toString()

            // Calcular y actualizar progresos
            val progreso = { cantidad: Int -> (cantidad * 15).coerceAtMost(100) }

            barraAgua.progress = progreso(conteo["agua"] ?: 0)
            barraComida.progress = progreso(conteo["comida"] ?: 0)
            barraSueno.progress = progreso(conteo["sueno"] ?: 0)
            barraEmociones.progress = progreso(conteo["correr"] ?: 0)
        }
    }
}
