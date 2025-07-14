package com.example.i_mail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmar: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        etConfirmar = findViewById(R.id.etConfirmar)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        db = BaseDeDatos(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmar = etConfirmar.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    esAdmin = false
                )

                val exito = db.registrarUsuario(nuevoUsuario)
                if (exito) {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: el correo ya existe", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
