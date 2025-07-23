package com.example.i_mail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

/**
 * Clase para generar archivos CSV del inventario y enviarlos por correo.
 * Diferencia entre exportaciones completas (admin) y por departamento (usuario).
 */

object ExportadorCSV {

    fun enviarPorCorreo(context: Context, archivo: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Inventario exportado")
            putExtra(Intent.EXTRA_TEXT, "Adjunto encontrarás el archivo CSV con el inventario.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Enviar Documento por..."))
    }

    fun exportarArticulosExtendido(context: Context, articulos: List<ArticuloExportado>): File? {
        return try {
            val archivo = File(context.getExternalFilesDir(null), "datos_exportados.csv")
            val writer = FileWriter(archivo)

            writer.append("ID,Nombre,Tipo,Cantidad,Estado,FechaIngreso,Asignado A,Fecha de Entrega\n")
            for (articulo in articulos) {
                writer.append("${articulo.id},${articulo.nombre},${articulo.tipo},${articulo.cantidad},${articulo.estado},${articulo.fechaIngreso},${articulo.asignadoA ?: "Disponible"},${articulo.fechaEntrega ?: "-"}\n")
            }

            writer.flush()
            writer.close()
            archivo
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al exportar CSV", Toast.LENGTH_SHORT).show()
            null
        }
    }

}
