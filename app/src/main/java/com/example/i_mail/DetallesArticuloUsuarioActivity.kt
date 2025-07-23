package com.example.i_mail

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

/**
 * Vista de solo lectura de artículos para usuarios regulares.
 * Muestra información completa sin opciones de modificación.
 */


class DetallesArticuloUsuarioActivity : AppCompatActivity() {

    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_articulo_usuario)

        db = BaseDeDatos(this)

        val ivImagen = findViewById<ImageView>(R.id.ivImagen)
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvTipo = findViewById<TextView>(R.id.tvTipo)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvCantidad = findViewById<TextView>(R.id.tvCantidad)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val tvAsignado = findViewById<TextView>(R.id.tvAsignado)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

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

        cargarImagenDesdeNombre(imagen, ivImagen)

        val entrega = db.obtenerEntregaDeArticulo(articuloId)
        if (entrega != null) {
            tvAsignado.text = "Asignado a: ${entrega.nombreUsuario} el ${entrega.fechaEntrega}"
        } else {
            tvAsignado.text = "Disponible (sin asignar)"
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuUsuarioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }
    fun cargarImagenDesdeNombre(imagen: String, imageView: ImageView) {
        try {
            if (imagen.startsWith("content://") || imagen.startsWith("file://")) {
                imageView.setImageURI(Uri.parse(imagen))
            } else {
                val resId = resources.getIdentifier(imagen, "drawable", packageName)
                if (resId != 0) {
                    imageView.setImageResource(resId)
                } else {
                    imageView.setImageResource(R.drawable.placeholder)
                }
            }
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.placeholder)
        }
    }
}
