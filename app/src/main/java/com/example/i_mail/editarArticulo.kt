package com.example.i_mail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class editarArticulo : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spTipo: Spinner
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
        spTipo = findViewById(R.id.spTipo)
        etCantidad = findViewById(R.id.etCantidad)
        btnGuardar = findViewById(R.id.btnGuardar)
        ivPreview = findViewById(R.id.ivPreview)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        db = BaseDeDatos(this)

        // Configurar opciones del Spinner
        val tipos = listOf("Hardware", "Herramienta")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipo.adapter = adaptador

        articuloId = intent.getIntExtra("articuloId", -1)
        if (articuloId != -1) {
            cargarDatosArticulo(articuloId, tipos)
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
            val tipo = spTipo.selectedItem.toString()
            val cantidadTexto = etCantidad.text.toString().trim()

            if (nombre.isEmpty() || cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toIntOrNull()
            if (cantidad == null || cantidad < 0) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val articuloExistente = db.obtenerArticuloPorId(articuloId)
            if (articuloExistente == null) {
                Toast.makeText(this, "No se encontró el artículo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val articuloActualizado = Articulo(
                id = articuloId,
                nombre = nombre,
                tipo = tipo,
                cantidad = cantidad,
                estado = articuloExistente.estado,
                fechaIngreso = articuloExistente.fechaIngreso,
                imagen = imagenUri ?: articuloExistente.imagen
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
                putExtra("id", articuloId)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun cargarDatosArticulo(id: Int, tipos: List<String>) {
        val articulo = db.obtenerArticuloPorId(id)
        if (articulo != null) {
            etNombre.setText(articulo.nombre)
            etCantidad.setText(articulo.cantidad.toString())
            imagenUri = articulo.imagen

            // Preseleccionar el tipo en el Spinner
            val posicionTipo = tipos.indexOf(articulo.tipo)
            if (posicionTipo != -1) {
                spTipo.setSelection(posicionTipo)
            }

            if (!imagenUri.isNullOrEmpty()) {
                val imagen = imagenUri!!  // Ya sabemos que no es nula ni vacía
                try {
                    if (imagen.startsWith("content://") || imagen.startsWith("file://")) {
                        ivPreview.setImageURI(Uri.parse(imagen))
                    } else {
                        val resId = resources.getIdentifier(imagen, "drawable", packageName)
                        if (resId != 0) {
                            ivPreview.setImageResource(resId)
                        } else {
                            ivPreview.setImageResource(R.drawable.placeholder)
                        }
                    }
                } catch (e: Exception) {
                    ivPreview.setImageResource(R.drawable.placeholder)
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
