package de.hsrm.mi.mobilecomputing.bookworm.features.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import de.hsrm.mi.mobilecomputing.bookworm.R

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    lateinit var settingsFragment : PreferenceFragmentCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        settingsFragment = SettingsFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.settingFragmentFL, settingsFragment)
            commit()
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        key?.let {
            val userAuth = FirebaseAuth.getInstance().currentUser
            if (it == "Darkmode") sharedPreferences?.let { pref ->
                if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            if (it == "E-Mail") sharedPreferences?.let { pref ->
                val newEmail = sharedPreferences.getString("E-Mail","")
                FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("E-Mail").setValue(newEmail)
                userAuth!!.updateEmail(newEmail.toString())
            }
            if (it == "Name") sharedPreferences?.let { pref ->
                FirebaseDatabase.getInstance().reference.child(firebaseRepo.getUser().toString()).child("Name").setValue(sharedPreferences.getString("Name",""))
            }
            if (it == "Password") sharedPreferences?.let { pref ->
                val newPW = sharedPreferences.getString("Password","")
                userAuth!!.updatePassword(newPW.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
    }


}