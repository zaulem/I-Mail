package com.example.i_mail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class FormularioArticuloActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etTipo: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etEstado: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnVolver: Button
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var ivPreview: ImageView

    private lateinit var db: BaseDeDatos
    private var imagenUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_articulo)

        etNombre = findViewById(R.id.etNombre)
        etTipo = findViewById(R.id.etTipo)
        etCantidad = findViewById(R.id.etCantidad)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnVolver = findViewById(R.id.btnVolver)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        ivPreview = findViewById(R.id.ivPreview)

        db = BaseDeDatos(this)

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, 100)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val tipo = etTipo.text.toString().trim()
            val cantidadTexto = etCantidad.text.toString().trim()
            val estado = "Disponible"

            if (nombre.isEmpty() || tipo.isEmpty() || cantidadTexto.isEmpty() || estado.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imagenUri == null) {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toIntOrNull()
            if (cantidad == null) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener la fecha actual en formato dd/MM/yyyy
            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val articulo = Articulo(
                nombre = nombre,
                tipo = tipo,
                cantidad = cantidad,
                estado = estado,
                fechaIngreso = fechaActual,
                imagen = imagenUri!!
            )

            val resultado = db.insertarArticulo(articulo)
            if (resultado != -1L) {
                Toast.makeText(this, "Artículo guardado exitosamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener { finish() }
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etTipo.text.clear()
        etCantidad.text.clear()
        etEstado.text.clear()
        ivPreview.setImageResource(R.drawable.placeholder)
        imagenUri = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imagenUri = uri.toString()
                ivPreview.setImageURI(uri)

                Toast.makeText(this, "Imagen seleccionada:\n$imagenUri", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
