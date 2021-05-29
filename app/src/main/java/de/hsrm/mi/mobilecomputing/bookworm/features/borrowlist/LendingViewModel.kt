package de.hsrm.mi.mobilecomputing.bookworm.features.borrowlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import de.hsrm.mi.mobilecomputing.bookworm.model.*

class LendingViewModel : ViewModel() {
    private val firebaseRepo = FirebaseRepo()
    private val lendingList: MutableLiveData<List<Lending>> by lazy {
        MutableLiveData<List<Lending>>().also {
            loadLendingList()
        }
    }

    private val borrowingList: MutableLiveData<List<Lending>> by lazy {
        MutableLiveData<List<Lending>>().also {
            loadBorrowingList()
        }
    }


    fun getLendingList(): LiveData<List<Lending>> {
        return lendingList
    }

    fun getBorrowingList(): LiveData<List<Lending>> {
        return borrowingList
    }


    fun loadLendingList() {
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString())

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren()) {
                    val lending = ArrayList<Lending>()
                    for(isbn in p0.child("LendingList").children){
                        val len: Lending = isbn.getValue(Lending::class.java)!!
                        len.isbn = isbn.key.toString()
                        lending.add(len)
                    }
                    lendingList.postValue(lending)
                }
            }

        })
    }

    fun loadBorrowingList() {
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString())

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren()) {
                    val borrowing = ArrayList<Lending>()
                    for(isbn in p0.child("BorrowList").children){
                        val borrow: Lending = isbn.getValue(Lending::class.java)!!
                        borrow.isbn = isbn.key.toString()
                        borrowing.add(borrow)
                    }
                    borrowingList.postValue(borrowing)
                }
            }

        })
    }

    fun sendReturnRequest(lending: Lending) {
        var query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
                .child(firebaseRepo.getUser().toString())
                .child("Outgoing")
                .child("Books")
                .child(lending.userUID)
                .child(lending.isbn)

        writeSingleProperty("Name", lending.bookTitle, query)
        writeSingleProperty("Author", lending.bookAuthor, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("ReturnBook", true, query)
        writeSingleProperty("BorrowBook", false, query)
        writeSingleProperty("Username", lending.userName, query)

        query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
                .child(lending.userUID)
                .child("Incoming")
                .child("Books")
                .child(firebaseRepo.getUser().toString())
                .child(lending.isbn)

        writeSingleProperty("Name", lending.bookTitle, query)
        writeSingleProperty("Author", lending.bookAuthor, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("ReturnBook", true, query)
        writeSingleProperty("BorrowBook", false, query)
        writeSingleProperty("Username", lending.userName, query)

    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }
}