package com.example.i_mail

data class Articulo(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val cantidad: Int,
    val estado: String,
    val fechaIngreso: String,
    val imagen: String
)
