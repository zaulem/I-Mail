package com.example.i_mail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class editarArticulo : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etTipo: EditText
    private lateinit var etCantidad: EditText
    private lateinit var btnGuardar: Button
    private lateinit var ivPreview: ImageView
    private lateinit var btnSeleccionarImagen: Button

    private lateinit var db: BaseDeDatos
    private var articuloId: Int = -1
    private var imagenUri: String? = null

    companion object {
        private const val REQUEST_CODE_SELECCIONAR_IMAGEN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_articulo)

        etNombre = findViewById(R.id.etNombre)
        etTipo = findViewById(R.id.etTipo)
        etCantidad = findViewById(R.id.etCantidad)
        btnGuardar = findViewById(R.id.btnGuardar)
        ivPreview = findViewById(R.id.ivPreview)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen) // Agrega este botón en tu layout
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        db = BaseDeDatos(this)

        articuloId = intent.getIntExtra("articuloId", -1)
        if (articuloId != -1) {
            cargarDatosArticulo(articuloId)
        } else {
            Toast.makeText(this, "Artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_SELECCIONAR_IMAGEN)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val tipo = etTipo.text.toString().trim()
            val cantidadTexto = etCantidad.text.toString().trim()

            if (nombre.isEmpty() || tipo.isEmpty() || cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toIntOrNull()
            if (cantidad == null || cantidad < 0) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val articuloActualizado = Articulo(
                id = articuloId,
                nombre = nombre,
                tipo = tipo,
                cantidad = cantidad,
                estado = "", // mantén el estado actual si quieres, o recupéralo antes
                fechaIngreso = "", // idem
                imagen = imagenUri ?: ""
            )

            val exito = db.actualizarArticulo(articuloActualizado)
            if (exito) {
                Toast.makeText(this, "Artículo actualizado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, DetalleArticuloActivity::class.java).apply {
                putExtra("id", articuloId) // Pasas el id del artículo para que detalle cargue los datos
            }
            startActivity(intent)
            finish() // Cierra la actividad actual para que no quede en backstack
        }

    }

    private fun cargarDatosArticulo(id: Int) {
        val articulo = db.obtenerArticuloPorId(id)
        if (articulo != null) {
            etNombre.setText(articulo.nombre)
            etTipo.setText(articulo.tipo)
            etCantidad.setText(articulo.cantidad.toString())
            imagenUri = articulo.imagen

            if (!imagenUri.isNullOrEmpty()) {
                try {
                    ivPreview.setImageURI(Uri.parse(imagenUri))
                } catch (e: Exception) {
                    ivPreview.setImageResource(R.drawable.placeholder) // o alguna imagen por defecto
                }
            }
        } else {
            Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECCIONAR_IMAGEN && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imagenUri = uri.toString()
                ivPreview.setImageURI(uri)
            } else {
                Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
