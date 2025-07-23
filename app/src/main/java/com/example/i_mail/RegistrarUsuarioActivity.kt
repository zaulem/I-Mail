package com.example.i_mail

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * Formulario de registro para nuevos usuarios del sistema.
 * Incluye validación de datos y asignación de departamentos.
 */

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var spDepartamento: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmar: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        spDepartamento = findViewById(R.id.spDepartamento)
        etPassword = findViewById(R.id.etPassword)
        etConfirmar = findViewById(R.id.etConfirmar)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        db = BaseDeDatos(this)

        // Cargar departamentos en el Spinner
        val departamentos = listOf("Mantenimiento", "IT")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDepartamento.adapter = adaptadorSpinner

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmar = etConfirmar.text.toString().trim()
            val departamentoSeleccionado = spDepartamento.selectedItem.toString()

            if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    departamento = departamentoSeleccionado,
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

        //Enlace para volver al login
        findViewById<TextView>(R.id.tvIrLogin).setOnClickListener {
            finish() // Vuelve al MainActivity (pantalla de login)
        }
    }
}