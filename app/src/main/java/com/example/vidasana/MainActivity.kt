package com.example.vidasana

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vidasana.adapters.HabitoAdapter
import com.example.vidasana.database.HabitoDatabase
import com.example.vidasana.databinding.ActivityMainBinding
import com.example.vidasana.activities.RegistroHabitosActivity
import com.example.vidasana.activities.EstadisticasActivity
import com.example.vidasana.login.LoginActivity
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Validar sesión iniciada
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerHabitos.layoutManager = LinearLayoutManager(this)

        binding.btnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistroHabitosActivity::class.java))
        }

        binding.btnEstadisticas.setOnClickListener {
            startActivity(Intent(this, EstadisticasActivity::class.java))
        }

        binding.btnGenerarPDF.setOnClickListener {
            generarPDF()
        }

        binding.btnSincronizar.setOnClickListener {
            sincronizarHabitos()
        }

        // ✅ Botón cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        cargarHabitos()
    }

    override fun onResume() {
        super.onResume()
        cargarHabitos()
    }

    private fun cargarHabitos() {
        lifecycleScope.launch {
            val usuarioId = obtenerUsuarioIdDesdePreferencias()
            val db = HabitoDatabase.getDatabase(this@MainActivity)
            val lista = db.habitoDao().obtenerPorUsuario(usuarioId)
            binding.recyclerHabitos.adapter = HabitoAdapter(lista) {
                cargarHabitos()
            }
        }
    }

    private fun obtenerUsuarioIdDesdePreferencias(): Int {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        return prefs.getInt("usuario_id", -1)
    }

    private fun sincronizarHabitos() {
        lifecycleScope.launch {
            val db = HabitoDatabase.getDatabase(this@MainActivity)
            val usuarioId = obtenerUsuarioIdDesdePreferencias()
            val lista = db.habitoDao().obtenerPorUsuario(usuarioId)

            lista.forEach {
                val json = JSONObject()
                json.put("tipo", it.tipo)
                json.put("cantidad", it.cantidad)
                json.put("fecha", it.fecha)
                json.put("usuario_id", usuarioId)

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = json.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:81/vidasana/insertar_habito.php")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("SYNC_ERROR", "Error: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val respuesta = response.body?.string()
                        Log.d("SYNC_RESPONSE", respuesta ?: "Sin respuesta")
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MainActivity, "Sincronizado correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        }
    }

    private fun generarPDF() {
        lifecycleScope.launch {
            val db = HabitoDatabase.getDatabase(this@MainActivity)
            val usuarioId = obtenerUsuarioIdDesdePreferencias()
            val lista = db.habitoDao().obtenerPorUsuario(usuarioId)

            if (lista.isEmpty()) {
                Toast.makeText(this@MainActivity, "No hay hábitos registrados", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "resumen_habitos.pdf")
                val pdfWriter = com.itextpdf.kernel.pdf.PdfWriter(file.absolutePath)
                val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = com.itextpdf.layout.Document(pdfDoc)

                document.add(com.itextpdf.layout.element.Paragraph("Resumen de Hábitos").setBold().setFontSize(18f))
                document.add(com.itextpdf.layout.element.Paragraph(" "))

                lista.forEach {
                    val texto = "Tipo: ${it.tipo}\nCantidad: ${it.cantidad}\nFecha: ${it.fecha}\n"
                    document.add(com.itextpdf.layout.element.Paragraph(texto))
                    document.add(com.itextpdf.layout.element.Paragraph(" "))
                }

                document.close()
                Toast.makeText(this@MainActivity, "PDF generado correctamente", Toast.LENGTH_LONG).show()
                abrirPDF(file)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Error al generar PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirPDF(file: File) {
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No hay visor de PDF instalado", Toast.LENGTH_LONG).show()
        }
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
