package es.manuelgonzalez.proyectobarberia.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.manuelgonzalez.proyectobarberia.data.model.Photo
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirestoreDB {

    companion object db {

        val database: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance()
        }

        /* ***************************************************
        *                   Database users                   *
        **************************************************** */

        fun addUser(user: User) {
            database.collection("users")
                .document(user.uid)
                .set(user)
                .addOnSuccessListener { Log.w("addUser", "Usuario añadido") }
                .addOnFailureListener { Log.e("addUser ", "Usuario no añadido") }
        }

        fun getUser(idUser: String): LiveData<User> {
            var mutableUser = MutableLiveData<User>()

            database.collection("users")
                .document(idUser.toString())
                .get()
                .addOnSuccessListener {
                    var user = User(
                        it.get("uid") as String,
                        it.get("fullName") as String,
                        it.get("email") as String,
                        it.get("telephone") as String,
                        it.get("activeDate") as Boolean,
                        it.get("admin") as Boolean,
                    )
                    mutableUser.value = user
                    Log.w("getUser", "Usuario obtenido")
                }
                .addOnCanceledListener {
                    Log.e("getUser", "No se ha obtenido el usuario")
                }

            return mutableUser
        }

        fun changeActiveDateCurrentUser(activeDate: Boolean) {
            database.collection("users")
                .document(firebaseAuth.uid!!)
                .update("activeDate", activeDate)
                .addOnSuccessListener { Log.w("changeActiveDateCurrentUser", "activeDate cambiada") }
                .addOnFailureListener { Log.e("changeActiveDateCurrentUser", "activeDate no cambiada") }
        }

        fun changeActiveDateUser(idUser: String, activeDate: Boolean) {
            database.collection("users")
                .document(idUser)
                .update("activeDate", activeDate)
                .addOnSuccessListener { Log.w("changeActiveDateUser", "activeDate cambiada") }
                .addOnFailureListener { Log.e("changeActiveDateUser", "activeDate no cambiada") }
        }


        /* ***************************************************
        *                   Database photos                   *
        **************************************************** */

        fun queryPhotos(): LiveData<List<Photo>> {
            var mutablePhotoList = MutableLiveData<List<Photo>>()
            database.collection("photo").get().addOnSuccessListener {
                var photoList: MutableList<Photo> = arrayListOf()
                for (photo in it) {
                    photoList.add(Photo(photo.get("id") as Long, photo.get("url") as String))
                    //photoList.add(photo.toObject(Photo::class.java))
                }
                Log.w("queryPhotos", "Fotos conseguidas")
                mutablePhotoList.value = photoList
            }
            return mutablePhotoList
        }

        /* ***************************************************
        *                   Database dates                   *
        **************************************************** */

        fun addDate(date: Date) {
            database.collection("dates")
                .document(date.idDate)
                .set(date)
                .addOnSuccessListener { Log.w("addDate", "Date añadida") }
                .addOnFailureListener { Log.e("addDate", "Date no añadida") }
        }

        fun deleteDate(idDate: String) {
            database.collection("dates")
                .document(idDate)
                .delete()
                .addOnSuccessListener { Log.w("deleteDate", "Date borrada") }
                .addOnFailureListener { Log.e("deleteDate", "Date no borrada") }
        }

        fun getActiveDate(user: User): LiveData<Date> {
            var mutableDate = MutableLiveData<Date>()
            database.collection("dates")
                .get()
                .addOnSuccessListener {
                    var datesList: MutableList<Date> = arrayListOf()
                    val nowDayTime = LocalDateTime.now()
                    for (date in it) {
                        Log.i("Cita user act", user.activeDate.toString())
                        var pattern = ""
                        var hourDate = date.get("hourDate") as String
                        if (hourDate.length == 5) {
                            pattern = "dd-MM-yyyy HH:mm"
                        } else {
                            pattern = "dd-MM-yyyy H:mm"
                        }
                        var dateDayTime = LocalDateTime.parse(
                            date.id,
                            DateTimeFormatter.ofPattern(pattern)
                        )
                        var idUser = date.get("idUser") as String
                        if (dateDayTime.isAfter(nowDayTime) && user.uid == idUser && user.activeDate) {
                            datesList.add(
                                Date(
                                    date.get("idDate") as String,
                                    date.get("idUser") as String,
                                    date.get("dayDate") as String,
                                    date.get("hourDate") as String,
                                )
                            )
                        }
                    }

                    Log.i("Cita", datesList[0].toString())
                    mutableDate.value = datesList[0]
                }
            return mutableDate
        }

        fun getDateByIdDate(idDate: String): LiveData<Date> {
            var mutableDate = MutableLiveData<Date>()
            database.collection("dates")
                .document(idDate)
                .get()
                .addOnSuccessListener {
                    var date = Date(
                        it.get("idDate") as String,
                        it.get("idUser") as String,
                        it.get("dayDate") as String,
                        it.get("hourDate") as String,
                    )
                    mutableDate.value = date
                }
            return mutableDate
        }

        fun queryDatesByDay(day: String): LiveData<List<Date>> {
            var mutableDateList = MutableLiveData<List<Date>>()
            database.collection("dates")
                .orderBy("idDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    var datesList: MutableList<Date> = arrayListOf()
                    for (date in it) {
                        if (date.get("dayDate") as String == day) {
                            datesList.add(
                                Date(
                                    date.get("idDate") as String,
                                    date.get("idUser") as String,
                                    date.get("dayDate") as String,
                                    date.get("hourDate") as String,
                                )
                            )
                        }
                    }
                    Log.i("Lista de citas filtradas", datesList.toString())
                    mutableDateList.value = datesList
                }
            return mutableDateList
        }

        // TODO
        fun queryHistoryDatesByUser(idUser: String): LiveData<List<Date>> {
            var mutableDateList = MutableLiveData<List<Date>>()
            database.collection("dates")
                .get()
                .addOnSuccessListener {
                    var datesList: MutableList<Date> = arrayListOf()
                    var dateTimeList: MutableList<LocalDateTime> = arrayListOf()
                    val nowDayTime = LocalDateTime.now()
                    for (date in it) {
                        var pattern = ""
                        var hourDate = date.get("hourDate") as String
                        if (hourDate.length == 5) {
                            pattern = "dd-MM-yyyy HH:mm"
                        } else {
                            pattern = "dd-MM-yyyy H:mm"
                        }
                        dateTimeList.add(
                            LocalDateTime.parse(
                                date.id,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                        )
                    }
                    dateTimeList.sort()
                    for (dateTime in dateTimeList) {
                        for (date in it) {
                            var pattern = ""
                            var hourDate = date.get("hourDate") as String
                            if (hourDate.length == 5) {
                                pattern = "dd-MM-yyyy HH:mm"
                            } else {
                                pattern = "dd-MM-yyyy H:mm"
                            }
                            var dateDayTime = LocalDateTime.parse(
                                date.id,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                            if (dateTime.isEqual(dateDayTime) && date.get("idUser") as String == idUser && dateDayTime.isBefore(
                                    nowDayTime
                                )
                            ) {
                                datesList.add(
                                    Date(
                                        date.get("idDate") as String,
                                        date.get("idUser") as String,
                                        date.get("dayDate") as String,
                                        date.get("hourDate") as String,
                                    )
                                )
                            }
                        }

                    }
                    mutableDateList.value = datesList.asReversed()
                }
            return mutableDateList
        }

        fun queryHistoryDates(): LiveData<List<Date>> {
            var mutableDateList = MutableLiveData<List<Date>>()
            database.collection("dates")
                .get()
                .addOnSuccessListener {
                    var datesList: MutableList<Date> = arrayListOf()
                    var dateTimeList: MutableList<LocalDateTime> = arrayListOf()
                    val nowDayTime = LocalDateTime.now()
                    for (date in it) {
                        var pattern = ""
                        var hourDate = date.get("hourDate") as String
                        if (hourDate.length == 5) {
                            pattern = "dd-MM-yyyy HH:mm"
                        } else {
                            pattern = "dd-MM-yyyy H:mm"
                        }
                        dateTimeList.add(
                            LocalDateTime.parse(
                                date.id,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                        )
                    }
                    dateTimeList.sort()
                    for (dateTime in dateTimeList) {
                        for (date in it) {
                            var pattern = ""
                            var hourDate = date.get("hourDate") as String
                            if (hourDate.length == 5) {
                                pattern = "dd-MM-yyyy HH:mm"
                            } else {
                                pattern = "dd-MM-yyyy H:mm"
                            }
                            var dateDayTime = LocalDateTime.parse(
                                date.id,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                            Log.i("Listado weno2", dateTime.toString())
                            Log.i("Listado weno3", dateDayTime.toString())
                            if (dateTime.isEqual(dateDayTime) && dateDayTime.isBefore(
                                    nowDayTime
                                )
                            ) {
                                datesList.add(
                                    Date(
                                        date.get("idDate") as String,
                                        date.get("idUser") as String,
                                        date.get("dayDate") as String,
                                        date.get("hourDate") as String,
                                    )
                                )
                            }
                        }

                    }
                    mutableDateList.value = datesList.asReversed()
                }
            return mutableDateList
        }


        /* ***************************************************
        *                   Database reviews                   *
        **************************************************** */

        fun getNewIdReview(): MutableLiveData<Long> {
            var mutableReviewId: MutableLiveData<Long> = MutableLiveData<Long>()
            database.collection("reviews")
                .orderBy("idReview", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener {
                    var idReview: Long = 0
                    for (review in it) {
                        idReview = (review.get("idReview") as String).toLong()
                    }
                    if (it.isEmpty) {
                        mutableReviewId.value = 1
                    } else {
                        mutableReviewId.value = idReview + 1
                    }
                }
            return mutableReviewId
        }

        fun addReview(review: Review) {
            database.collection("reviews")
                .document(review.idReview)
                .set(review)
                .addOnSuccessListener { Log.w("addReview", "Review añadida") }
                .addOnFailureListener { Log.e("addReview", "Review no añadida") }
        }

        fun queryReviews(): LiveData<List<Review>> {
            var mutableReviewList = MutableLiveData<List<Review>>()
            database.collection("reviews")
                .get()
                .addOnSuccessListener {
                    var reviewsList: MutableList<Review> = arrayListOf()
                    var dateTimeList: MutableList<LocalDateTime> = arrayListOf()
                    val nowDayTime = LocalDateTime.now()
                    for (review in it) {
                        var pattern = ""
                        var hourDate = review.get("idDate") as String
                        if (hourDate.length == 16) {
                            pattern = "dd-MM-yyyy HH:mm"
                        } else {
                            pattern = "dd-MM-yyyy H:mm"
                        }
                        dateTimeList.add(
                            LocalDateTime.parse(
                                review.get("idDate") as String,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                        )
                    }
                    dateTimeList.sort()
                    for (dateTime in dateTimeList) {
                        for (review in it) {
                            var pattern = ""
                            var hourDate = review.get("idDate") as String
                            if (hourDate.length == 16) {
                                pattern = "dd-MM-yyyy HH:mm"
                            } else {
                                pattern = "dd-MM-yyyy H:mm"
                            }
                            var dateDayTime = LocalDateTime.parse(
                                review.get("idDate") as String,
                                DateTimeFormatter.ofPattern(pattern)
                            )
                            if (dateTime.isEqual(dateDayTime) && dateDayTime.isBefore(
                                    nowDayTime
                                )
                            ) {
                                reviewsList.add(
                                    Review(
                                        review.get("idReview") as String,
                                        review.get("idUser") as String,
                                        review.get("idDate") as String,
                                        (review.get("rating") as Double).toFloat(),
                                        review.get("reviewText") as String,
                                        review.get("userName") as String
                                    )
                                )
                            }
                        }

                    }

                    mutableReviewList.value = reviewsList.asReversed()
                }
            return mutableReviewList
        }

        fun getReviewByIdDate(idDate: String): MutableLiveData<Review?> {
            var mutableReview = MutableLiveData<Review?>()
            database.collection("reviews")
                .get()
                .addOnSuccessListener {
                    var reviewList: MutableList<Review> = arrayListOf()
                    for (review in it) {
                        if (review.get("idDate") as String == idDate) {
                            reviewList.add(
                                Review(
                                    review.get("idReview") as String,
                                    review.get("idUser") as String,
                                    review.get("idDate") as String,
                                    (review.get("rating") as Double).toFloat(),
                                    review.get("reviewText") as String,
                                    review.get("userName") as String
                                )
                            )
                        }
                    }
                    Log.i("Review", reviewList.toString())
                    if (reviewList.isEmpty())
                        mutableReview.value = null
                    else
                        mutableReview.value = reviewList[0]
                }
            return mutableReview
        }
    }

}