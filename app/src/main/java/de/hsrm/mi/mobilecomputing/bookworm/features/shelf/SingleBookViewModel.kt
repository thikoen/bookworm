package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo

class SingleBookViewModel : ViewModel () {

    private val firebaseRepo = FirebaseRepo()

    fun writeReadingProperty (isbn : String, reading : Boolean, wishToRead : Boolean, finishedReading : Boolean){
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString())
                .child("Books")
                .child(isbn)
        writeSingleProperty("Reading", reading, query)
        writeSingleProperty("WishToRead", wishToRead, query)
        writeSingleProperty("FinishedReading", finishedReading, query)
    }

    fun deleteBook(isbn : String){
        val query = FirebaseDatabase
                .getInstance()
                .reference
                .child(firebaseRepo.getUser().toString())
                .child("Books")
        query.child(isbn).removeValue()
    }

    fun setLikeDisliked(book: Book){
        val query = FirebaseDatabase
                .getInstance()
                .reference
                .child(firebaseRepo.getUser().toString())
                .child("Books")
                .child(book.isbn)
        writeSingleProperty("Liked", book.liked, query)
        writeSingleProperty("Disliked", book.disliked, query)
    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }
}