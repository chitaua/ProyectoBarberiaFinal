package es.manuelgonzalez.proyectobarberia.ui.historyDates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth

class HistoryDatesFragmentViewModel : ViewModel() {

    val currentUser: LiveData<User> = FirestoreDB.getUser(firebaseAuth.uid!!)
    val datesHistory: LiveData<List<Date>> = FirestoreDB.queryHistoryDates()
    val datesHistoryByCurrentUser: LiveData<List<Date>> =
        FirestoreDB.queryHistoryDatesByUser(firebaseAuth.uid!!)

    fun getReviewByIdDate(idDate: String): MutableLiveData<Review?> {
        return FirestoreDB.getReviewByIdDate(idDate)
    }
}