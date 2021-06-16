package es.manuelgonzalez.proyectobarberia.ui.dates

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth

class DatesFragmentViewModel : ViewModel() {

    val user: LiveData<User> = FirestoreDB.getUser(firebaseAuth.uid!!)

    fun addDate(date: Date) {
        FirestoreDB.addDate(date)
    }

    fun deleteDate(idDate: String) {
        FirestoreDB.deleteDate(idDate)
    }

    fun changeActiveDate(activeDate: Boolean) {
        FirestoreDB.changeActiveDateCurrentUser(activeDate)
    }

    fun getActiveDateByUser(user: User): LiveData<Date> {
        return FirestoreDB.getActiveDate(user)
    }

    fun queryDatesByDay(dia: String): LiveData<List<Date>> {
        return FirestoreDB.queryDatesByDay(dia)
    }


}
