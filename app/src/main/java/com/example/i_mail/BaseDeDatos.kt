package com.example.i_mail

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "InventarioMailDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                esAdmin INTEGER DEFAULT 0,
                departamento TEXT NOT NULL
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

        UsuarioPorDefecto(db)
        ArticulosPorDefecto(db)
    }

    private fun UsuarioPorDefecto(db: SQLiteDatabase) {
        val admin = ContentValues().apply {
            put("nombre", "Admin")
            put("correo", "admin@gmail.com")
            put("password", "admin123")
            put("esAdmin", 1)
            put("departamento", "Administración")
        }
        db.insert("usuarios", null, admin)

        val usuarioIT = ContentValues().apply {
            put("nombre", "Carlos Mendoza")
            put("correo", "carlos@gmail.com")
            put("password", "it123")
            put("esAdmin", 0)
            put("departamento", "IT")
        }
        db.insert("usuarios", null, usuarioIT)

        val usuarioMantenimiento = ContentValues().apply {
            put("nombre", "Luis González")
            put("correo", "luis@gmail.com")
            put("password", "mant123")
            put("esAdmin", 0)
            put("departamento", "Mantenimiento")
        }
        db.insert("usuarios", null, usuarioMantenimiento)
    }

    private fun ArticulosPorDefecto(db: SQLiteDatabase) {
        val articulos = listOf(
            ContentValues().apply {
                put("nombre", "Mouse con cable")
                put("tipo", "Hardware")
                put("cantidad", 3)
                put("estado", "Disponible")
                put("fechaIngreso", "2025-07-20")
                put("imagen", "mousefoto")
            },
            ContentValues().apply {
                put("nombre", "Teclado con cable")
                put("tipo", "Hardware")
                put("cantidad", 2)
                put("estado", "Disponible")
                put("fechaIngreso", "2025-07-20")
                put("imagen", "tecladofoto")
            },
            ContentValues().apply {
                put("nombre", "Taladro")
                put("tipo", "Herramienta")
                put("cantidad", 2)
                put("estado", "Disponible")
                put("fechaIngreso", "2025-07-20")
                put("imagen", "taladrofoto")
            },
            ContentValues().apply {
                put("nombre", "Caja de Herramientas")
                put("tipo", "Herramienta")
                put("cantidad", 1)
                put("estado", "Disponible")
                put("fechaIngreso", "2025-07-20")
                put("imagen", "cajafoto")
            }
        )

        for (articulo in articulos) {
            db.insert("articulos", null, articulo)
        }
    }


    //Metodos para consultas de la base de datos
    fun validarUsuario(correo: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE correo = ? AND password = ?",
            arrayOf(correo, password)
        )

        return if (cursor.moveToFirst()) {
            Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                correo = correo,
                password = password,
                esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1,
                departamento = cursor.getString(cursor.getColumnIndexOrThrow("departamento"))
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
            put("departamento", usuario.departamento)
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
                    esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("esAdmin")) == 1,
                    departamento = cursor.getString(cursor.getColumnIndexOrThrow("departamento"))
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

    fun obtenerArticulosPorDepartamento(departamento: String): List<Articulo> {
        val db = readableDatabase

        // Mapeo simple entre departamento y tipo de artículo
        val tipoRelacionado = when (departamento) {
            "IT" -> "Hardware"
            "Mantenimiento" -> "Herramienta"
            else -> "Otro"
        }

        val cursor = db.rawQuery("SELECT * FROM articulos WHERE tipo = ?", arrayOf(tipoRelacionado))
        val lista = mutableListOf<Articulo>()

        while (cursor.moveToNext()) {
            lista.add(
                Articulo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                    imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                )
            )
        }

        cursor.close()
        return lista
    }

    fun marcarArticuloComoDisponible(articuloId: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            // 1. Eliminar TODOS los registros de entregas asociadas al artículo
            val entregasEliminadas = db.delete("entregas", "articuloId = ?", arrayOf(articuloId.toString()))

            // 2. Actualizar estado del artículo a "Disponible"
            val valores = ContentValues().apply {
                put("estado", "Disponible")
            }
            val filasActualizadas = db.update("articulos", valores, "id = ?", arrayOf(articuloId.toString()))

            // 3. Confirmar transacción si ambas operaciones fueron exitosas
            if (filasActualizadas > 0) {
                db.setTransactionSuccessful()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // En caso de error, la transacción se revierte automáticamente
            false
        } finally {
            db.endTransaction()
        }
    }

    fun actualizarArticulo(articulo: Articulo): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", articulo.nombre)
            put("tipo", articulo.tipo)
            put("cantidad", articulo.cantidad)
            put("estado", articulo.estado)
            put("fechaIngreso", articulo.fechaIngreso)
            put("imagen", articulo.imagen)
        }
        val filas = db.update("articulos", valores, "id = ?", arrayOf(articulo.id.toString()))
        return filas > 0
    }

    fun obtenerArticuloPorId(id: Int): Articulo? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM articulos WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            Articulo(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
            )
        } else null
    }

    fun obtenerArticulosExportablesAdmin(): List<ArticuloExportado> {
        val db = readableDatabase
        val query = """
        SELECT a.id, a.nombre, a.tipo, a.cantidad, a.estado, a.fechaIngreso,
               u.nombre AS asignadoA, e.fechaEntrega
        FROM articulos a
        LEFT JOIN entregas e ON a.id = e.articuloId
        LEFT JOIN usuarios u ON e.usuarioId = u.id
        GROUP BY a.id
    """
        val cursor = db.rawQuery(query, null)
        val lista = mutableListOf<ArticuloExportado>()

        while (cursor.moveToNext()) {
            val articulo = ArticuloExportado(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                asignadoA = cursor.getString(cursor.getColumnIndexOrThrow("asignadoA")),
                fechaEntrega = cursor.getString(cursor.getColumnIndexOrThrow("fechaEntrega"))
            )
            lista.add(articulo)
        }

        cursor.close()
        return lista
    }

    fun obtenerArticulosExportablesPorDepartamento(departamento: String): List<ArticuloExportado> {
        val db = readableDatabase

        val tipoRelacionado = when (departamento) {
            "IT" -> "Hardware"
            "Mantenimiento" -> "Herramienta"
            else -> "Otro"
        }

        val query = """
        SELECT a.id, a.nombre, a.tipo, a.cantidad, a.estado, a.fechaIngreso,
               u.nombre AS asignadoA, e.fechaEntrega
        FROM articulos a
        LEFT JOIN entregas e ON a.id = e.articuloId
        LEFT JOIN usuarios u ON e.usuarioId = u.id
        WHERE a.tipo = ?
        GROUP BY a.id
    """

        val cursor = db.rawQuery(query, arrayOf(tipoRelacionado))
        val lista = mutableListOf<ArticuloExportado>()

        while (cursor.moveToNext()) {
            val articulo = ArticuloExportado(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                asignadoA = cursor.getString(cursor.getColumnIndexOrThrow("asignadoA")),
                fechaEntrega = cursor.getString(cursor.getColumnIndexOrThrow("fechaEntrega"))
            )
            lista.add(articulo)
        }

        cursor.close()
        return lista
    }



    fun eliminarArticulo(id: Int): Boolean {
        val db = writableDatabase

        // eliminar las entregas asociadas
        db.delete("entregas", "articuloId = ?", arrayOf(id.toString()))

        // eliminar el artículo
        val filasAfectadas = db.delete("articulos", "id = ?", arrayOf(id.toString()))

        return filasAfectadas > 0
    }

    // ====== FUNCIONES DE ESTADÍSTICAS ======
// Agregar estas funciones al final de tu clase BaseDeDatos, antes del onUpgrade

    /**
     * Cuenta el total de artículos registrados en el sistema
     * @return Número total de artículos
     */
    fun contarTotalArticulos(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM articulos", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    /**
     * Cuenta artículos por estado específico
     * @param estado El estado a contar (ej: "Disponible", "Asignado")
     * @return Número de artículos con ese estado
     */
    fun contarArticulosPorEstado(estado: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM articulos WHERE estado = ?",
            arrayOf(estado)
        )
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    /**
     * Obtiene estadísticas completas del inventario
     * @return Map con todas las estadísticas
     */
    fun obtenerEstadisticasCompletas(): Map<String, Int> {
        val db = readableDatabase
        val estadisticas = mutableMapOf<String, Int>()

        // Total de artículos
        val cursorTotal = db.rawQuery("SELECT COUNT(*) FROM articulos", null)
        if (cursorTotal.moveToFirst()) {
            estadisticas["total"] = cursorTotal.getInt(0)
        }
        cursorTotal.close()

        // Conteo por estados
        val cursorEstados = db.rawQuery(
            "SELECT estado, COUNT(*) FROM articulos GROUP BY estado",
            null
        )

        // Inicializar contadores en 0
        estadisticas["disponible"] = 0
        estadisticas["asignado"] = 0
        estadisticas["mantenimiento"] = 0
        estadisticas["retirado"] = 0

        while (cursorEstados.moveToNext()) {
            val estado = cursorEstados.getString(0)?.lowercase() ?: ""
            val cantidad = cursorEstados.getInt(1)

            when (estado) {
                "disponible" -> estadisticas["disponible"] = cantidad
                "asignado" -> estadisticas["asignado"] = cantidad
                "mantenimiento" -> estadisticas["mantenimiento"] = cantidad
                "retirado" -> estadisticas["retirado"] = cantidad
            }
        }
        cursorEstados.close()

        return estadisticas
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS entregas")
        db.execSQL("DROP TABLE IF EXISTS articulos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }
}
