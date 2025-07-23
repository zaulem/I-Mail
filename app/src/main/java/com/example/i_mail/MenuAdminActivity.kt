package com.example.i_mail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * Dashboard principal para administradores con estadísticas en tiempo real.
 * Permite gestión completa del inventario y acceso a todas las funcionalidades.
 */


class MenuAdminActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)

        sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE)
        db = BaseDeDatos(this)
        listView = findViewById(R.id.listViewArticulos)

        cargarArticulos()

        // Botón: Registrar nuevo artículo
        findViewById<Button>(R.id.btnRegistrarArticulo).setOnClickListener {
            startActivity(Intent(this, FormularioArticuloActivity::class.java))
        }

        // Botón: Exportar CSV
        findViewById<Button>(R.id.btnExportarCSV).setOnClickListener {
            val articulosExportables = db.obtenerArticulosExportablesAdmin()
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

        //Botón Actualizar (opcional)
        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            cargarArticulos()
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarArticulos()
    }

    private fun cargarArticulos() {
        val articulos = db.obtenerArticulos()
        val adapter = ArticuloAdapter(this, articulos)
        listView.adapter = adapter

        if (articulos.isEmpty()) {
            Toast.makeText(this, "No hay artículos registrados", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val articulo = articulos[position]
            val intent = Intent(this, DetalleArticuloActivity::class.java).apply {
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

        //Actualizar estadísticas después de cargar artículos
        actualizarEstadisticas()
    }

    //Actualizar las estadísticas en las cards
    private fun actualizarEstadisticas() {
        try {
            // Obtener estadísticas de la base de datos
            val totalArticulos = db.contarTotalArticulos()
            val disponibles = db.contarArticulosPorEstado("Disponible")
            val asignados = db.contarArticulosPorEstado("Asignado")

            // Actualizar los TextViews de estadísticas
            findViewById<TextView>(R.id.tvTotalArticulos).text = totalArticulos.toString()
            findViewById<TextView>(R.id.tvDisponibles).text = disponibles.toString()
            findViewById<TextView>(R.id.tvAsignados).text = asignados.toString()

        } catch (e: Exception) {
            // En caso de error, mostrar 0 en las estadísticas
            findViewById<TextView>(R.id.tvTotalArticulos).text = "0"
            findViewById<TextView>(R.id.tvDisponibles).text = "0"
            findViewById<TextView>(R.id.tvAsignados).text = "0"

            // Opcional: mostrar mensaje de error solo en desarrollo
            // Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cerrarSesion() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}