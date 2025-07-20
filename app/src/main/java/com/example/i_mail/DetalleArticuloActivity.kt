package com.example.i_mail

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

class DetalleArticuloActivity : AppCompatActivity() {

    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_articulo)

        db = BaseDeDatos(this)

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
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        val btnMenu = findViewById<Button>(R.id.btnMenu)

        val articuloId = intent.getIntExtra("id", -1)
        val nombre = intent.getStringExtra("nombre") ?: "Sin nombre"
        val tipo = intent.getStringExtra("tipo") ?: "Sin tipo"
        val estado = intent.getStringExtra("estado") ?: "Sin estado"
        val cantidad = intent.getIntExtra("cantidad", 0)
        val fecha = intent.getStringExtra("fechaIngreso") ?: "Sin fecha"
        val imagen = intent.getStringExtra("imagen") ?: ""

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

        val entrega = db.obtenerEntregaDeArticulo(articuloId)
        if (entrega != null) {
            tvAsignado.text = "Asignado a: ${entrega.nombreUsuario} el ${entrega.fechaEntrega}"
        } else {
            tvAsignado.text = "Disponible (sin asignar)"
        }

        btnAsignar.setOnClickListener {
            val usuarios = db.obtenerUsuarios()

            if (usuarios.isEmpty()) {
                Toast.makeText(this, "No hay usuarios disponibles para asignar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nombresConDisponible = mutableListOf("Disponible")
            nombresConDisponible.addAll(usuarios.map { it.nombre })

            AlertDialog.Builder(this)
                .setTitle("Asignar a un usuario")
                .setItems(nombresConDisponible.toTypedArray()) { _, which ->
                    val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                    if (which == 0) {
                        // Opción "Disponible"
                        val exito = db.marcarArticuloComoDisponible(articuloId)
                        if (exito) {
                            Toast.makeText(this, "Artículo marcado como disponible", Toast.LENGTH_SHORT).show()
                            tvAsignado.text = "Disponible (sin asignar)"
                            tvEstado.text = "Estado: Disponible"
                        } else {
                            Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val usuarioSeleccionado = usuarios[which - 1] // Ajuste de índice
                        val exito = db.asignarArticuloAUsuario(articuloId, usuarioSeleccionado.id, fechaHoy)

                        if (exito) {
                            Toast.makeText(this, "Artículo asignado a ${usuarioSeleccionado.nombre}", Toast.LENGTH_SHORT).show()
                            tvAsignado.text = "Asignado a: ${usuarioSeleccionado.nombre} el $fechaHoy"
                            tvEstado.text = "Estado: Asignado"
                        } else {
                            Toast.makeText(this, "Error al asignar el artículo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, editarArticulo::class.java).apply {
                putExtra("articuloId", articuloId)
            }
            startActivity(intent)

        }



        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar artículo")
                .setMessage("¿Estás seguro de que deseas eliminar este artículo? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí") { _, _ ->
                    val exito = db.eliminarArticulo(articuloId)
                    if (exito) {
                        Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ListaArticulosActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al eliminar el artículo", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, ListaArticulosActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuAdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
