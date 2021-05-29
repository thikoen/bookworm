package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import de.hsrm.mi.mobilecomputing.bookworm.model.Book

class ShelfViewModel : ViewModel() {
    private val firebaseRepo = FirebaseRepo()
    private var buddyUID = String()
    private val books: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>().also {
            loadBooks()
        }
    }

    private val buddyBooks: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>().also {
            loadBuddyBooks()
        }
    }

    fun setBuddyUID(uid: String){
        buddyUID = uid
    }
    fun getBooks(): LiveData<List<Book>> {
        return books
    }

    fun getBuddyBooks(): LiveData<List<Book>> {
        return buddyBooks
    }

    fun loadBooks() {
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString()).child("Books")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val bookList = ArrayList<Book>()
                if (p0.hasChildren()) {
                    for (snapshot in p0.children) {
                        var book = snapshot.getValue(Book::class.java)!!
                        book.isbn = snapshot.key.toString()
                        bookList.add(book)
                    }
                }
                books.apply { books.value = bookList }
            }
        })
    }

    fun loadBuddyBooks() {
        val query = FirebaseDatabase.getInstance().reference
            .child(buddyUID).child("Books")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val bookList = ArrayList<Book>()
                if (p0.hasChildren()) {
                    for (snapshot in p0.children) {
                        val book = snapshot.getValue(Book::class.java)!!
                        book.isbn = snapshot.key.toString()
                        bookList.add(book)
                    }
                }
                buddyBooks.apply { buddyBooks.value = bookList }
            }
        })
    }

    fun getBookWithISBN(isbn: String) : Book {
        var specificBook = Book()
        for (book in books.value!!) {
            if (book.isbn == isbn){
                specificBook = book
            }
        }
        return specificBook
    }

    fun sendBookBorrowRequest(friendUID: String, username: String, book: Book){
        var query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
            .child(firebaseRepo.getUser().toString())
            .child("Outgoing")
            .child("Books")
            .child(friendUID)
            .child(book.isbn)

        writeSingleProperty("Name", book.name, query)
        writeSingleProperty("Author", book.author, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("ReturnBook", false, query)
        writeSingleProperty("BorrowBook", true, query)
        writeSingleProperty("Username", username, query)

        query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
            .child(friendUID)
            .child("Incoming")
            .child("Books")
            .child(firebaseRepo.getUser().toString())
            .child(book.isbn)

        writeSingleProperty("Name", book.name, query)
        writeSingleProperty("Author", book.author, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("ReturnBook", false, query)
        writeSingleProperty("BorrowBook", true, query)
        writeSingleProperty("Username", username, query)
    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }
}