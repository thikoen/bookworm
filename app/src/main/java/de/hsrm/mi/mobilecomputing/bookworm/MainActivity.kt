package de.hsrm.mi.mobilecomputing.bookworm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        val navigationController = findNavController(R.id.navhostFragment)
        if (navigationController.currentDestination?.id == R.id.navhostFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}