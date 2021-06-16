package es.manuelgonzalez.proyectobarberia.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import es.manuelgonzalez.proyectobarberia.data.FirestoreDB
import es.manuelgonzalez.proyectobarberia.data.model.Photo

class GalleryFragmentViewModel : ViewModel() {

    val photos: LiveData<List<Photo>> = FirestoreDB.queryPhotos()

}