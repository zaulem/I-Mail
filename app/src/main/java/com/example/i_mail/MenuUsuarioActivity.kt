package com.example.i_mail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * Menú principal para usuarios regulares con vista filtrada por departamento.
 * Proporciona acceso limitado según permisos del rol asignado.
 */


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

        // NUEVO: Configurar bienvenida personalizada
        configurarBienvenida()

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

        //Botón actualizar (opcional)
        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            cargarArticulos()
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarArticulos()
    }

    //Configurar bienvenida personalizada
    private fun configurarBienvenida() {
        try {
            // Obtener nombre del usuario desde SharedPreferences
            val nombreUsuario = sharedPreferences.getString("nombre", "Usuario") ?: "Usuario"

            // Configurar textos de bienvenida
            findViewById<TextView>(R.id.tvBienvenidaUsuario).text = "Bienvenido, $nombreUsuario"
            findViewById<TextView>(R.id.tvDepartamentoUsuario).text = "Departamento: $departamento"

        } catch (e: Exception) {
            // En caso de error, mostrar valores por defecto
            findViewById<TextView>(R.id.tvBienvenidaUsuario).text = "Bienvenido, Usuario"
            findViewById<TextView>(R.id.tvDepartamentoUsuario).text = "Departamento: $departamento"
        }
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

        // Actualizar estadísticas del departamento
        actualizarEstadisticasDepartamento()
    }

    // Actualizar estadísticas del departamento
    private fun actualizarEstadisticasDepartamento() {
        try {
            // Obtener artículos del departamento para contar estadísticas
            val articulosDepartamento = db.obtenerArticulosPorDepartamento(departamento)

            val totalArticulos = articulosDepartamento.size
            val disponibles = articulosDepartamento.count { it.estado == "Disponible" }
            val asignados = articulosDepartamento.count { it.estado == "Asignado" }

            // Actualizar los TextViews de estadísticas
            findViewById<TextView>(R.id.tvTotalArticulos).text = totalArticulos.toString()
            findViewById<TextView>(R.id.tvDisponibles).text = disponibles.toString()
            findViewById<TextView>(R.id.tvAsignados).text = asignados.toString()

        } catch (e: Exception) {
            // En caso de error, mostrar 0 en las estadísticas
            findViewById<TextView>(R.id.tvTotalArticulos).text = "0"
            findViewById<TextView>(R.id.tvDisponibles).text = "0"
            findViewById<TextView>(R.id.tvAsignados).text = "0"
        }
    }
    //Botón para cerrar sesión
    private fun cerrarSesion() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}