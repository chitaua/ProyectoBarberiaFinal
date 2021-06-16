package es.manuelgonzalez.proyectobarberia.ui.userInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.User

class UserInfoFragmentViewModel: ViewModel() {

    fun getDatebyIdDate(idDate: String): LiveData<Date> = FirestoreDB.getDateByIdDate(idDate)

    fun getUser(idUser: String): LiveData<User> = FirestoreDB.getUser(idUser)
    fun deleteDate(idDate: String) = FirestoreDB.deleteDate(idDate)
    fun changeActiveDateUser(idUser: String, isActive: Boolean) = FirestoreDB.changeActiveDateUser(idUser, isActive)

}