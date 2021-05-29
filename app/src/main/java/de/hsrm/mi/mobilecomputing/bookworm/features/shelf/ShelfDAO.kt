package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.Shelf
import kotlinx.coroutines.*


data class ShelfDAO(val userID : String){

    //Saving and accessing book data and more from storage
    private lateinit var database: DatabaseReference

    private fun initDB(){
        database = Firebase.database.reference
    }

    fun getShelfFromStorage(): Deferred<Shelf> = GlobalScope.async {
        initDB()
        val shelf = Shelf(emptyList())
        database.child(userID).child("Books").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val book = Book()
                        book.isbn = snapshot.key.toString()

                        GlobalScope.launch {
                            delay(500)
                            shelf.addBook(book)
                        }
                    }
                }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        return@async shelf
    }

    fun shelfToStorage(shelf: Shelf){
        //Connect to firebase DB
        initDB()

        //Save current shelf
        for (book in shelf.books) {
            val query =
            database
                .child(userID)
                .child("Books")
                .child(book.isbn)
            writeSingleProperty("Author", book.author, query)
            writeSingleProperty("Name", book.name, query)
            writeSingleProperty("Cover", book.thumbail, query)
        }
    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }
}