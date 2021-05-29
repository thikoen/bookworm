package de.hsrm.mi.mobilecomputing.bookworm.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseRepo {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val username: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            loadUsername()
        }
    }

    private val userId: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            loadId()
        }
    }

    fun getUsername(): String {
        return username.value.toString()
    }

    fun getId(): String {
        return userId.value.toString()
    }

    init {
        loadUsername()
        loadId()
    }

        fun loadUsername() {
            val query = FirebaseDatabase.getInstance().reference
                    .child(getUser().toString())

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        val name = p0.child("Name").value.toString()
                        username.postValue(name)
                    }
                }

            })
        }

    fun loadId() {
        val query = FirebaseDatabase.getInstance().reference
                .child(getUser().toString())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    val id = p0.child("Id").value.toString()
                    userId.postValue(id)
                }
            }

        })
    }

    fun getEmail(): String {
        return firebaseAuth.currentUser?.email.toString()
    }
    fun getUser(): String? {
        return firebaseAuth.currentUser?.uid
    }
}