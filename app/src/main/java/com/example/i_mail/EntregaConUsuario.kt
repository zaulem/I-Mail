package com.example.i_mail

/**
 * Combina información de entregas con datos del usuario asignado.
 * Optimiza las consultas que requieren mostrar asignaciones de artículos.
 */

data class EntregaConUsuario(
    val nombreUsuario: String,
    val fechaEntrega: String
)
