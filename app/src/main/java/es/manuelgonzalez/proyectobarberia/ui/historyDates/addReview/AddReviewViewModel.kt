package es.manuelgonzalez.proyectobarberia.ui.historyDates.addReview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth

class AddReviewViewModel : ViewModel() {

    val currentUser: LiveData<User> = FirestoreDB.getUser(firebaseAuth.currentUser!!.uid)
    val newIdReview: LiveData<Long> = FirestoreDB.getNewIdReview()

    fun addReview(review: Review) {
        FirestoreDB.addReview(review)
    }
}