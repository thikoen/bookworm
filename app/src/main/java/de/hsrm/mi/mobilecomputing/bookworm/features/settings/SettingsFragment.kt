package de.hsrm.mi.mobilecomputing.bookworm.features.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.hsrm.mi.mobilecomputing.bookworm.R

class SettingsFragment : PreferenceFragmentCompat (){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}