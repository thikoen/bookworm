package de.hsrm.mi.mobilecomputing.bookworm.features.readinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo

class ReadinglistViewModel : ViewModel(){
    private val firebaseRepo = FirebaseRepo()
    private val readingBooks: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>().also {
            loadBooks()
        }
    }
    private val wishToReadBooks: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>().also {
            loadBooks()
        }
    }
    private val finishedBooks: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>().also {
            loadBooks()
        }
    }

    fun getReadingBooks(): LiveData<List<Book>> {
        return readingBooks
    }
    fun getWishToReadBooks(): LiveData<List<Book>> {
        return wishToReadBooks
    }
    fun getFinishedBooks(): LiveData<List<Book>> {
        return finishedBooks
    }

    fun loadBooks() {
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString()).child("Books")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val readingList = ArrayList<Book>()
                val wishToReadList = ArrayList<Book>()
                val finishedList = ArrayList<Book>()
                if (p0.hasChildren()) {
                    for (snapshot in p0.children) {
                            if(snapshot.hasChild("Reading")){
                                if(snapshot.child("Reading").value as Boolean){
                                    val book = snapshot.getValue(Book::class.java)!!
                                    book.isbn = snapshot.key.toString()
                                    readingList.add(book)
                                }
                            }
                            if(snapshot.hasChild("WishToRead")) {
                                if(snapshot.child("WishToRead").value as Boolean) {
                                    val book = snapshot.getValue(Book::class.java)!!
                                    book.isbn = snapshot.key.toString()
                                    wishToReadList.add(book)
                                }
                            }
                            if(snapshot.hasChild("FinishedReading")) {
                                if(snapshot.child("FinishedReading").value as Boolean){
                                    val book = snapshot.getValue(Book::class.java)!!
                                    book.isbn = snapshot.key.toString()
                                    finishedList.add(book)
                                }
                            }

                    }
                }
                readingBooks.apply { readingBooks.value = readingList }
                wishToReadBooks.apply { wishToReadBooks.value = wishToReadList }
                finishedBooks.apply { finishedBooks.value = finishedList }
            }
        })
    }
}