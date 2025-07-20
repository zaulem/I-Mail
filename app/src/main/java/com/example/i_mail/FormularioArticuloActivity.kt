package com.example.i_mail

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class FormularioArticuloActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spTipo: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnVolver: Button
    private lateinit var ivPreview: ImageView

    private lateinit var db: BaseDeDatos
    private var imagenUri: String? = null
    private val CODIGO_SELECCION_IMAGEN = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_articulo)

        etNombre = findViewById(R.id.etNombre)
        spTipo = findViewById(R.id.spTipo)
        etCantidad = findViewById(R.id.etCantidad)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        btnVolver = findViewById(R.id.btnVolver)
        ivPreview = findViewById(R.id.ivPreview)

        db = BaseDeDatos(this)

        // Configurar opciones del Spinner
        val tipos = listOf("Hardware", "Herramienta")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipo.adapter = adaptador

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, CODIGO_SELECCION_IMAGEN)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val tipo = spTipo.selectedItem.toString()
            val cantidadTexto = etCantidad.text.toString().trim()
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (nombre.isEmpty() || cantidadTexto.isEmpty() || imagenUri == null) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toIntOrNull()
            if (cantidad == null || cantidad < 0) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoArticulo = Articulo(
                nombre = nombre,
                tipo = tipo,
                cantidad = cantidad,
                estado = "Disponible",
                fechaIngreso = fechaActual,
                imagen = imagenUri!!
            )

            val resultado = db.insertarArticulo(nuevoArticulo)
            if (resultado != -1L) {
                Toast.makeText(this, "Artículo guardado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_SELECCION_IMAGEN && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imagenUri = uri.toString()
                ivPreview.setImageURI(uri)
            }
        }
    }
}
