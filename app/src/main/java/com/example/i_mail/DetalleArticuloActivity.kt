package com.example.i_mail

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalleArticuloActivity : AppCompatActivity() {

    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_articulo)

        db = BaseDeDatos(this)

        // Obtener referencias de los views
        val ivImagen = findViewById<ImageView>(R.id.ivImagen)
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvTipo = findViewById<TextView>(R.id.tvTipo)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvCantidad = findViewById<TextView>(R.id.tvCantidad)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val tvAsignado = findViewById<TextView>(R.id.tvAsignado)
        val btnAsignar = findViewById<Button>(R.id.btnAsignar)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        // Obtener datos del artículo desde el intent
        val articuloId = intent.getIntExtra("id", -1)
        val nombre = intent.getStringExtra("nombre") ?: "Sin nombre"
        val tipo = intent.getStringExtra("tipo") ?: "Sin tipo"
        val estado = intent.getStringExtra("estado") ?: "Sin estado"
        val cantidad = intent.getIntExtra("cantidad", 0)
        val fecha = intent.getStringExtra("fechaIngreso") ?: "Sin fecha"
        val imagen = intent.getStringExtra("imagen") ?: ""

        // Mostrar datos
        tvNombre.text = nombre
        tvTipo.text = "Tipo: $tipo"
        tvEstado.text = "Estado: $estado"
        tvCantidad.text = "Cantidad: $cantidad"
        tvFecha.text = "Fecha de ingreso: $fecha"

        try {
            if (imagen.startsWith("content://") || imagen.startsWith("file://")) {
                ivImagen.setImageURI(Uri.parse(imagen))
            } else {
                val resId = resources.getIdentifier(imagen, "drawable", packageName)
                if (resId != 0) {
                    ivImagen.setImageResource(resId)
                } else {
                    ivImagen.setImageResource(R.drawable.placeholder)
                }
            }
        } catch (e: Exception) {
            ivImagen.setImageResource(R.drawable.placeholder)
        }

        // Consultar asignación actual
        val entrega = db.obtenerEntregaDeArticulo(articuloId)
        if (entrega != null) {
            tvAsignado.text = "Asignado a: ${entrega.nombreUsuario} el ${entrega.fechaEntrega}"
        } else {
            tvAsignado.text = "Disponible (sin asignar)"
        }

        // Botones funcionales
        btnAsignar.setOnClickListener {
            val usuarios = db.obtenerUsuarios()

            if (usuarios.isEmpty()) {
                Toast.makeText(this, "No hay usuarios disponibles para asignar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nombres = usuarios.map { it.nombre }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Asignar a un usuario")
                .setItems(nombres) { _, which ->
                    val usuarioSeleccionado = usuarios[which]
                    val fechaHoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

                    val valores = ContentValues().apply {
                        put("articuloId", articuloId)
                        put("usuarioId", usuarioSeleccionado.id)
                        put("fechaEntrega", fechaHoy)
                    }

                    val resultado = db.writableDatabase.insert("entregas", null, valores)
                    if (resultado != -1L) {
                        Toast.makeText(this, "Artículo asignado a ${usuarioSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
                        tvAsignado.text = "Asignado a: ${usuarioSeleccionado.nombre} el $fechaHoy"
                    } else {
                        Toast.makeText(this, "Error al asignar el artículo", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


        btnEditar.setOnClickListener {
            Toast.makeText(this, "Aquí irá la edición del artículo", Toast.LENGTH_SHORT).show()
            // Aquí se implementará más adelante
        }

        btnEliminar.setOnClickListener {
            Toast.makeText(this, "Aquí se implementará la eliminación", Toast.LENGTH_SHORT).show()
            // Confirmación + eliminación
        }
    }
}
