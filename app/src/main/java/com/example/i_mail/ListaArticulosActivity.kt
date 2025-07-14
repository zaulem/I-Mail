package com.example.i_mail

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListaArticulosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var db: BaseDeDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_articulos)

        listView = findViewById(R.id.listViewArticulos)
        db = BaseDeDatos(this)

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
    }
}
