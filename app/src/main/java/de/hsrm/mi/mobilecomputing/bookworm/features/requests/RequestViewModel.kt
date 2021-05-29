package de.hsrm.mi.mobilecomputing.bookworm.features.requests

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.*
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import de.hsrm.mi.mobilecomputing.bookworm.model.Request
import de.hsrm.mi.mobilecomputing.bookworm.model.User
import java.util.*
import kotlin.collections.ArrayList

class RequestViewModel: ViewModel() {

    private val firebaseRepo = FirebaseRepo()
    private var singleBook = Book()
    private val requests: MutableLiveData<List<Request>> by lazy {
        MutableLiveData<List<Request>>().also {
            loadRequests()
        }
    }

    init {
        loadRequests()
    }
    var unseenRequests : MutableLiveData<Boolean> = MutableLiveData(false)

    fun getRequests(): LiveData<List<Request>> {
        return requests
    }

    fun getSingleBook(): Book {
        return singleBook
    }

    fun loadRequests() {
        val query = FirebaseDatabase.getInstance().reference

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val requestList = ArrayList<Request>()
                var unseenfriendRequest = false
                var unseenbookRequest = false
                if (p0.hasChildren()) {
                    for (userUID in p0.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Books").children) {
                        for(isbnCode in userUID.children){
                            val book = Book()
                            val user = User()
                            book.isbn = isbnCode.key.toString()
                            book.name = isbnCode.child("Name").value.toString()
                            book.author = isbnCode.child("Author").value.toString()
                            user.name = isbnCode.child("Username").value.toString()
                            user.UID = userUID.key.toString()
                            requestList.add(Request(user, book, isbnCode.child("ReturnBook").value as Boolean, isbnCode.child("BorrowBook").value as Boolean,  isbnCode.child("MessageRead").value as Boolean))
                            unseenbookRequest = true
                        }
                    }
                    for (friendRequest in p0.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Friends").children) {
                        val user = User()
                        user.UID = friendRequest.key.toString()
                        user.name = friendRequest.child("Name").value.toString()
                        user.email = friendRequest.child("E-Mail").value.toString()
                        user.id = friendRequest.child("Id").value.toString()
                        requestList.add(Request(user, messageRead = friendRequest.child("MessageRead").value as Boolean))
                        unseenfriendRequest = true
                    }
                }
                if (unseenfriendRequest || unseenbookRequest){
                    unseenRequests.postValue(true)
                } else{
                    unseenRequests.postValue(false)
                }
                requests.postValue(requestList)
            }

        })
    }

    fun markRequestAsRead(req: Request) {
        //Check if only friend request
        if(req.borrowBook || req.returnBook) {
            FirebaseDatabase.getInstance().reference.child("Requestqueue")
                    .child(firebaseRepo.getUser().toString())
                    .child("Incoming")
                    .child("Books")
                    .child(req.user.UID)
                    .child(req.book.isbn)
                    .child("MessageRead")
                    .setValue(true)
        } else {
            FirebaseDatabase.getInstance().reference.child("Requestqueue")
                    .child(firebaseRepo.getUser().toString())
                    .child("Incoming")
                    .child("Friends")
                    .child(req.user.UID)
                    .child("MessageRead")
                    .setValue(true)
        }
    }

    fun acceptRequest(req: Request){
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        when {
            req.returnBook -> {
                var query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Books")
                query.child(req.user.UID).child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("Books").child(req.book.isbn)
                writeSingleProperty("Lended", false, query)
                query = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("LendingList")
                query.child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(req.user.UID).child("Outgoing").child("Books").child(firebaseRepo.getUser().toString())
                query.child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child(req.user.UID).child("BorrowList")
                query.child(req.book.isbn).removeValue()
            }
            req.borrowBook -> {
                var query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Books")
                query.child(req.user.UID).child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("Books").child(req.book.isbn)
                writeSingleProperty("Lended", true, query)
                writeSingleProperty("Reading", false, query)
                query = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("LendingList").child(req.book.isbn)
                writeSingleProperty("Date", currentDate, query)
                writeSingleProperty("User", req.user.name, query)
                writeSingleProperty("UserUID", req.user.UID, query)
                writeSingleProperty("Title", req.book.name, query)
                writeSingleProperty("Author", req.book.author, query)
                query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(req.user.UID).child("Outgoing").child("Books").child(firebaseRepo.getUser().toString())
                query.child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child(req.user.UID).child("BorrowList").child(req.book.isbn)
                writeSingleProperty("Date", currentDate, query)
                writeSingleProperty("User", firebaseRepo.getUsername(), query)
                writeSingleProperty("UserUID", firebaseRepo.getUser(), query)
                writeSingleProperty("Title", req.book.name, query)
                writeSingleProperty("Author", req.book.author, query)
            }
            else -> {
                var query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Friends")
                query.child(req.user.UID).removeValue()
                query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(req.user.UID).child("Outgoing").child("Friends")
                query.child(firebaseRepo.getUser().toString()).removeValue()
                query = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("Buddys").child(req.user.id.toString())
                writeSingleProperty("E-Mail", req.user.email, query)
                writeSingleProperty("Name", req.user.name.toString(), query)
                writeSingleProperty("UID", req.user.UID, query)
                query = FirebaseDatabase.getInstance().reference.child(req.user.UID).child("Buddys").child(firebaseRepo.getId())
                writeSingleProperty("E-Mail", firebaseRepo.getEmail(), query)
                writeSingleProperty("Name", firebaseRepo.getUsername(), query)
                writeSingleProperty("UID", firebaseRepo.getUser().toString(), query)
            }
        }
    }

    fun declineRequest(req: Request){
        when {
            req.returnBook || req.borrowBook -> {
                var query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Books")
                query.child(req.user.UID).child(req.book.isbn).removeValue()
                query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(req.user.UID).child("Outgoing").child("Books").child(firebaseRepo.getUser().toString())
                query.child(req.book.isbn).removeValue()
            }
            else -> {
                var query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(firebaseRepo.getUser().toString()).child("Incoming").child("Friends")
                query.child(req.user.UID).removeValue()
                query = FirebaseDatabase.getInstance().reference.child("Requestqueue").child(req.user.UID).child("Outgoing").child("Friends")
                query.child(firebaseRepo.getUser().toString()).removeValue()
            }
        }
    }

    fun getSingleBook(isbn: String) {
        val query  = FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("Books")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.child(isbn).getValue(Book::class.java)!!
                singleBook = book
            }
        })
    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }
}