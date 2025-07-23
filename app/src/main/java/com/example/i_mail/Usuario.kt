package com.example.i_mail

/**
 * Representa un usuario del sistema con roles diferenciados.
 * Maneja tanto administradores como usuarios regulares por departamento.
 */

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val esAdmin: Boolean = false,
    val departamento: String
)
