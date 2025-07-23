package com.example.i_mail

/**
 * Representa un artículo del inventario.
 * Contiene información básica como nombre, tipo, cantidad, estado e imagen.
 */
data class Articulo(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val cantidad: Int,
    val estado: String,
    val fechaIngreso: String,
    val imagen: String
)
