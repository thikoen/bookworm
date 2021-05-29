package de.hsrm.mi.mobilecomputing.bookworm.features.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hsrm.mi.mobilecomputing.bookworm.MainActivity
import de.hsrm.mi.mobilecomputing.bookworm.R
import kotlinx.android.synthetic.main.activity_login.et_register_email
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_register)
        register_button.setOnClickListener {

            when {

                //Error handling first: Check for Name, Mail, PW and for Equality of PW
                TextUtils.isEmpty(et_register_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please enter name.",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_register_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please enter E-Mail.",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_register_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please enter Password.",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_register_password_confirmed.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                            this@RegisterActivity,
                            "Please confirm Password.",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                et_register_password.text
                        .toString()
                        .equals(et_register_password_confirmed.text.toString())
                        .not() -> {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Passwords are not equal.",
                                Toast.LENGTH_SHORT
                            ).show()
                }

                //Everything correct
                else -> {

                    val name: String = et_register_name.text.toString().trim { it <= ' ' }
                    val email: String = et_register_email.text.toString().trim { it <= ' ' }
                    val password: String = et_register_password.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener { task ->
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task_next  ->

                                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                            val database: DatabaseReference = Firebase.database.reference
                                            database.child(uid).child("Name").setValue(name)
                                            database.child(uid).child("E-Mail").setValue(email)

                                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                            intent.putExtra(
                                                    "user_id",
                                                    FirebaseAuth.getInstance().currentUser!!.uid
                                            )

                                            generateID()

                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            Toast.makeText(
                                                    this@RegisterActivity,
                                                    "Successfully registered.",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(intent)
                                            finish()
                                        }
                            }
                }
            }
        }
    }

    fun generateID(){
        //Generate ID
            val randomID = (1000..9999).random()
            //ID already in DB?
            val database: DatabaseReference = Firebase.database.reference
            database.orderByChild("Id").equalTo(randomID.toString()).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                   if (!snapshot.exists()){
                       //ID Free
                       database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("Id").setValue(randomID)
                   } else {
                      generateID()
                   }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}