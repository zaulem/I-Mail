package com.example.i_mail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MenuUsuarioActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var db: BaseDeDatos
    private lateinit var departamento: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_usuario)

        sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE)
        db = BaseDeDatos(this)
        listView = findViewById(R.id.listViewArticulos)



        // Obtener el departamento del usuario desde la sesión
        departamento = sharedPreferences.getString("departamento", "") ?: ""

        cargarArticulos()

        // Botón: Exportar CSV filtrado
        findViewById<Button>(R.id.btnExportarCSV).setOnClickListener {
            val articulosExportables = db.obtenerArticulosExportablesPorDepartamento(departamento)
            if (articulosExportables.isEmpty()) {
                Toast.makeText(this, "No hay artículos para exportar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val archivo = ExportadorCSV.exportarArticulosExtendido(this, articulosExportables)
            if (archivo != null) {
                ExportadorCSV.enviarPorCorreo(this, archivo)
            }
        }

        // Botón: Cerrar sesión
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarArticulos()
    }

    private fun cargarArticulos() {
        val articulos = db.obtenerArticulosPorDepartamento(departamento)
        val adapter = ArticuloAdapter(this, articulos)
        listView.adapter = adapter

        if (articulos.isEmpty()) {
            Toast.makeText(this, "No hay artículos registrados para tu departamento", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val articulo = articulos[position]
            val intent = Intent(this, DetallesArticuloUsuarioActivity::class.java).apply {
                putExtra("id", articulo.id)
                putExtra("nombre", articulo.nombre)
                putExtra("tipo", articulo.tipo)
                putExtra("cantidad", articulo.cantidad)
                putExtra("estado", articulo.estado)
                putExtra("fechaIngreso", articulo.fechaIngreso)
                putExtra("imagen", articulo.imagen)
            }
            startActivity(intent)
        }
    }

    private fun cerrarSesion() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
