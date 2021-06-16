package es.manuelgonzalez.proyectobarberia.ui.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.data.model.User

class ReviewsFragmentViewModel: ViewModel() {

    val reviews: LiveData<List<Review>> = FirestoreDB.queryReviews()
    fun getReview(idDate: String): MutableLiveData<Review?> = FirestoreDB.getReviewByIdDate(idDate)
    fun getReviewUser(idUser: String): LiveData<User> = FirestoreDB.getUser(idUser)
}