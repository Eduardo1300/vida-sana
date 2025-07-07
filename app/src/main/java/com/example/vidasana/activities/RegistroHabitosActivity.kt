package com.example.vidasana.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vidasana.database.HabitoDatabase
import com.example.vidasana.databinding.ActivityRegistroHabitosBinding
import com.example.vidasana.models.Habito
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistroHabitosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroHabitosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroHabitosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Obtener usuarioId desde sesión guardada
        val sharedPrefs = getSharedPreferences("session", MODE_PRIVATE)
        val usuarioId = sharedPrefs.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Sesión no iniciada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnGuardar.setOnClickListener {
            val tipo = binding.etTipo.text.toString().trim()
            val cantidad = binding.etCantidad.text.toString().trim()

            if (tipo.isEmpty() || cantidad.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val nuevoHabito = Habito(tipo = tipo, cantidad = cantidad, fecha = fecha, usuarioId = usuarioId)

            lifecycleScope.launch {
                val db = HabitoDatabase.getDatabase(this@RegistroHabitosActivity)
                db.habitoDao().insertar(nuevoHabito)
                Toast.makeText(this@RegistroHabitosActivity, "¡Registrado!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
