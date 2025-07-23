package com.example.i_mail

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

/**
 * Adaptador personalizado para mostrar listas de artículos en ListView.
 * Maneja la visualización de imágenes y datos de cada artículo.
 */

class ArticuloAdapter(context: Context, private val articulos: List<Articulo>) :
    ArrayAdapter<Articulo>(context, 0, articulos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val articulo = articulos[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_articulo, parent, false)

        val ivImagen = view.findViewById<ImageView>(R.id.ivArticulo)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvTipo = view.findViewById<TextView>(R.id.tvTipo)
        val tvCantidad = view.findViewById<TextView>(R.id.tvCantidad)
        val tvEstado = view.findViewById<TextView>(R.id.tvEstado)

        tvNombre.text = articulo.nombre
        tvTipo.text = "Tipo: ${articulo.tipo}"
        tvCantidad.text = "Cantidad: ${articulo.cantidad}"
        tvEstado.text = "Estado: ${articulo.estado}"

        try {
            if (articulo.imagen.startsWith("content://") || articulo.imagen.startsWith("file://")) {
                ivImagen.setImageURI(Uri.parse(articulo.imagen))
            } else {
                val resId = context.resources.getIdentifier(articulo.imagen, "drawable", context.packageName)
                if (resId != 0) {
                    ivImagen.setImageResource(resId)
                } else {
                    ivImagen.setImageResource(R.drawable.placeholder)
                }
            }
        } catch (e: Exception) {
            ivImagen.setImageResource(R.drawable.placeholder)
        }

        return view
    }
}
