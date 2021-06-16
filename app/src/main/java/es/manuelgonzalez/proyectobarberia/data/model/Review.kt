package es.manuelgonzalez.proyectobarberia.data.model

data class Review(
    var idReview: String,
    val idUser: String,
    var idDate: String,
    val rating: Float,
    val reviewText: String )
