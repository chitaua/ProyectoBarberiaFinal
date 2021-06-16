package es.manuelgonzalez.proyectobarberia.data.model

import java.util.*

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    val telephone: String,
    val activeDate: Boolean,
    val admin: Boolean = false)
