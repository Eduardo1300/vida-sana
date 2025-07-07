package com.example.vidasana.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vidasana.databinding.ActivityRegistroBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrarse.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            if (nombre.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario(nombre, clave)
            }
        }

        binding.tvIrALogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registrarUsuario(nombre: String, clave: String) {
        val json = JSONObject()
        json.put("usuario", nombre)
        json.put("clave", clave)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://10.0.2.2:81/vidasana/registro_usuario.php")
            .post(body)
            .build()

        val client = OkHttpClient()
        CoroutineScope(Dispatchers.IO).launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@RegistroActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val respuesta = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful && respuesta?.contains("success") == true) {
                            Toast.makeText(this@RegistroActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegistroActivity, "Error: $respuesta", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }
}