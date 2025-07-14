package com.example.i_mail

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val esAdmin: Boolean = false
)
