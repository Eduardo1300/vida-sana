package com.example.vidasana.login

import android.content.Intent
import android.os.Bundle
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vidasana.MainActivity
import com.example.vidasana.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsuario: EditText
    private lateinit var etClave: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnIrRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnLogin = findViewById(R.id.btnLogin)
        btnIrRegistro = findViewById(R.id.btnIrRegistro)

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val clave = etClave.text.toString()
            loginUsuario(usuario, clave)
        }

        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun loginUsuario(usuario: String, clave: String) {
        val json = JSONObject()
        json.put("usuario", usuario)
        json.put("clave", clave)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://10.0.2.2:81/vidasana/login_usuario.php")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respuesta = response.body?.string()
                val jsonResp = JSONObject(respuesta ?: "")
                if (jsonResp.getBoolean("success")) {
                    val idUsuario = jsonResp.getInt("id")
                    val prefs = getSharedPreferences("session", MODE_PRIVATE)
                    prefs.edit().putInt("usuario_id", idUsuario).apply()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}