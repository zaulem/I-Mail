package com.example.i_mail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuAdminActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)

        sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE)

        findViewById<Button>(R.id.btnRegistrarArticulo).setOnClickListener {
            startActivity(Intent(this, FormularioArticuloActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerInventario).setOnClickListener {
            startActivity(Intent(this, ListaArticulosActivity::class.java))
        }

        findViewById<Button>(R.id.btnExportarCSV).setOnClickListener {
            val db = BaseDeDatos(this)
            val articulos = db.obtenerArticulosExportablesAdmin()

            if (articulos.isEmpty()) {
                Toast.makeText(this, "No hay artículos para exportar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val archivo = ExportadorCSV.exportarArticulosExtendido(this, articulos)
            if (archivo != null) {
                ExportadorCSV.enviarPorCorreo(this, archivo)
            }
        }




        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
