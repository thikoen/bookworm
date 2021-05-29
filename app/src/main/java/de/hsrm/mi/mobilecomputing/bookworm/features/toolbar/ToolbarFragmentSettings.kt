package de.hsrm.mi.mobilecomputing.bookworm.features.toolbar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.settings.SettingsActivity

class ToolbarFragmentSettings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_toolbar_settings, container, false)

        view.findViewById<ImageView>(R.id.settingIV).setOnClickListener {
            val settingsItent = Intent (activity, SettingsActivity::class.java)
            activity?.startActivity(settingsItent)
        }
        return view
    }
}