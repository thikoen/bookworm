package de.hsrm.mi.mobilecomputing.bookworm.features.buddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import de.hsrm.mi.mobilecomputing.bookworm.model.User

class BuddyViewModel : ViewModel() {
    private val firebaseRepo = FirebaseRepo()
    private val buddys: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadBuddys()
        }
    }

    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            searchUsers()
        }
    }

    fun getBuddys(): LiveData<List<User>> {
        return buddys
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }


    fun loadBuddys() {
        val query = FirebaseDatabase.getInstance().reference
                .child(firebaseRepo.getUser().toString())

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChildren()) {
                    val buddyList = ArrayList<User>()
                    for(snapshot in p0.child("Buddys").children){
                        val user: User = snapshot.getValue(User::class.java)!!
                        user.id = snapshot.key
                        buddyList.add(user)
                    }
                    buddys.postValue(buddyList)
                }
            }

        })
    }

    fun searchUsers(){
        val query = FirebaseDatabase.getInstance().reference
        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val resultList: ArrayList<User> = ArrayList()
                val queueList: ArrayList<String> = ArrayList()
                if(p0.hasChildren()) {
                    for(res in p0.child("Requestqueue").children){
                        if (res.key == firebaseRepo.getUser().toString()) {
                            for(user in res.child("Outgoing").child("Friends").children) {
                                queueList.add(user.key.toString())
                            }
                            for(user in res.child("Incoming").child("Friends").children) {
                                queueList.add(user.key.toString())
                            }
                        }
                    }
                    for(buddy in p0.child(firebaseRepo.getUser().toString()).child("Buddys").children) {
                        queueList.add(buddy.child("UID").value.toString())
                    }
                    for(snapshot in p0.children){
                        if ((snapshot.key.toString() != "Requestqueue") && snapshot.key.toString() !in queueList && snapshot.key.toString() != firebaseRepo.getUser().toString())
                        {
                            val user = User()
                            user.id = snapshot.child("Id").value.toString()
                            user.UID = snapshot.key.toString()
                            user.email = snapshot.child("E-Mail").value.toString()
                            user.name = snapshot.child("Name").value.toString()
                            resultList.add(user)
                        }
                    }
                }
              users.apply { users.value = resultList }
            }

        })
    }

    fun sendFriendRequest(user: User){
        var query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
            .child(firebaseRepo.getUser().toString())
            .child("Outgoing")
            .child("Friends")
            .child(user.UID)

        writeSingleProperty("Name", user.name, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("ReturnBook", false, query)
        writeSingleProperty("Id", user.id, query)
        writeSingleProperty("E-Mail", user.email, query)

        query = FirebaseDatabase.getInstance().reference.child("Requestqueue")
            .child(user.UID)
            .child("Incoming")
            .child("Friends")
            .child(firebaseRepo.getUser().toString())

        writeSingleProperty("Name", firebaseRepo.getUsername(), query)
        writeSingleProperty("E-Mail", firebaseRepo.getEmail(), query)
        writeSingleProperty("ReturnBook", false, query)
        writeSingleProperty("MessageRead", false, query)
        writeSingleProperty("Id", firebaseRepo.getId(), query)
    }

    private fun <T> writeSingleProperty(path: String, value: T, query: DatabaseReference){
        query.child(path).setValue(value)
    }

}
