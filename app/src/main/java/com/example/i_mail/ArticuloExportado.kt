package com.example.i_mail

data class ArticuloExportado(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val cantidad: Int,
    val estado: String,
    val fechaIngreso: String,
    val asignadoA: String?, // null si no está asignado
    val fechaEntrega: String? // null si no ha sido entregado
)
