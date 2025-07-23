package com.example.i_mail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/**
 * Pantalla de inicio de sesión con validación de credenciales.
 * Redirige a menús específicos según el rol del usuario autenticado.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnCrearCuenta: Button

    private lateinit var db: BaseDeDatos
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SharedPreferences antes de cualquier lógica
        sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE)
        val sesionActiva = sharedPreferences.getBoolean("sesionActiva", false)

        // Si ya hay sesión activa, redirigir automáticamente
        if (sesionActiva) {
            val esAdmin = sharedPreferences.getBoolean("esAdmin", false)
            val intent = if (esAdmin) {
                Intent(this, MenuAdminActivity::class.java)
            } else {
                Intent(this, MenuUsuarioActivity::class.java)
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la vista
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)

        // Inicialización de la base de datos
        db = BaseDeDatos(this)

        // Botón: Iniciar sesión
        btnIniciarSesion.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val usuario = db.validarUsuario(correo, password)
                if (usuario != null) {
                    // Guardar la sesión
                    val editor = sharedPreferences.edit()
                    editor.putInt("idUsuario", usuario.id)
                    editor.putString("nombre", usuario.nombre)
                    editor.putString("correo", usuario.correo)
                    editor.putBoolean("esAdmin", usuario.esAdmin)
                    editor.putString("departamento", usuario.departamento)
                    editor.putBoolean("sesionActiva", true)
                    editor.apply()

                    // Redirigir según el rol
                    if (usuario.esAdmin) {
                        startActivity(Intent(this, MenuAdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, MenuUsuarioActivity::class.java))
                    }

                    finish()
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón: Crear cuenta
        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegistrarUsuarioActivity::class.java))
        }
    }
}
