package com.example.i_mail

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "InventarioMailDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                esAdmin INTEGER DEFAULT 0
            )
        """)

        // Tabla de artículos
        db.execSQL("""
            CREATE TABLE articulos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                tipo TEXT,
                cantidad INTEGER,
                estado TEXT,
                fechaIngreso TEXT,
                imagen TEXT
                
            )
        """)

        // Tabla de entregas
        db.execSQL("""
            CREATE TABLE entregas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                articuloId INTEGER,
                usuarioId INTEGER,
                fechaEntrega TEXT,
                FOREIGN KEY (articuloId) REFERENCES articulos(id),
                FOREIGN KEY (usuarioId) REFERENCES usuarios(id)
            )
        """)

        insertarAdminPorDefecto(db)
    }

    private fun insertarAdminPorDefecto(db: SQLiteDatabase) {
        val admin = ContentValues().apply {
            put("nombre", "Admin")
            put("correo", "admin@gmail.com")
            put("password", "admin123") // En producción, esto debería ir cifrado
            put("esAdmin", 1)
        }
        db.insert("usuarios", null, admin)
    }

    //Metodos para consultas de la base de datos
    fun validarUsuario(correo: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE correo = ? AND password = ?", arrayOf(correo, password))

        return if (cursor.moveToFirst()) {
            Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                correo = correo,
                password = password,
                esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
            )
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun registrarUsuario(usuario: Usuario): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("correo", usuario.correo)
            put("password", usuario.password)
            put("esAdmin", if (usuario.esAdmin) 1 else 0)
        }
        val resultado = db.insert("usuarios", null, valores)
        return resultado != -1L
    }

    fun insertarArticulo(articulo: Articulo): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", articulo.nombre)
            put("tipo", articulo.tipo)
            put("cantidad", articulo.cantidad)
            put("estado", articulo.estado)
            put("fechaIngreso", articulo.fechaIngreso)
            put("imagen", articulo.imagen)
        }
        return db.insert("articulos", null, valores)
    }

    fun obtenerUsuarios(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios", null)

        if (cursor.moveToFirst()) {
            do {
                val usuario = Usuario(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1
                )
                usuarios.add(usuario)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return usuarios
    }



    fun obtenerArticulos(): List<Articulo> {
        val articulos = mutableListOf<Articulo>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM articulos", null)

        if (cursor.moveToFirst()) {
            do {
                val articulo = Articulo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                    imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                )
                articulos.add(articulo)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return articulos
    }

    fun obtenerEntregaDeArticulo(articuloId: Int): EntregaConUsuario? {
        val db = readableDatabase
        val consulta = """
        SELECT e.fechaEntrega, u.nombre 
        FROM entregas e
        JOIN usuarios u ON e.usuarioId = u.id
        WHERE e.articuloId = ?
        ORDER BY e.id DESC
        LIMIT 1
    """
        val cursor = db.rawQuery(consulta, arrayOf(articuloId.toString()))

        return if (cursor.moveToFirst()) {
            EntregaConUsuario(
                nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                fechaEntrega = cursor.getString(cursor.getColumnIndexOrThrow("fechaEntrega"))
            )
        } else null
    }

    fun asignarArticuloAUsuario(articuloId: Int, usuarioId: Int, fechaEntrega: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val valoresEntrega = ContentValues().apply {
                put("articuloId", articuloId)
                put("usuarioId", usuarioId)
                put("fechaEntrega", fechaEntrega)
            }
            val resultadoEntrega = db.insert("entregas", null, valoresEntrega)

            val valoresEstado = ContentValues().apply {
                put("estado", "Asignado")
            }
            val resultadoEstado = db.update("articulos", valoresEstado, "id = ?", arrayOf(articuloId.toString()))

            if (resultadoEntrega != -1L && resultadoEstado > 0) {
                db.setTransactionSuccessful()  // confirma la transacción
                true
            } else {
                false
            }
        } finally {
            db.endTransaction()  // termina la transacción (confirma o revierte)
        }
    }

    fun marcarArticuloComoDisponible(articuloId: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("estado", "Disponible")
        }
        val filasActualizadas = db.update("articulos", valores, "id = ?", arrayOf(articuloId.toString()))
        return filasActualizadas > 0
    }







    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS entregas")
        db.execSQL("DROP TABLE IF EXISTS articulos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }
}
