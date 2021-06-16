package es.manuelgonzalez.proyectobarberia.data.model

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    val telephone: String,
    val activeDate: Boolean,
    val admin: Boolean = false)
