package de.hsrm.mi.mobilecomputing.bookworm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragmentSettings
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
    private val firebaseRepo = FirebaseRepo()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View =  inflater.inflate(R.layout.fragment_home, container, false)
        val usernameView = view.findViewById<TextView>(R.id.textView)
        val nameObserver = Observer<String> { newName ->
            val greetingText = "Welcome, ${newName}!"
            usernameView.text = greetingText
        }

        firebaseRepo.username.observe(viewLifecycleOwner, nameObserver)
        view.shelfIV.setOnClickListener {
            val actionHomeToShelf = HomeFragmentDirections.actionHomeFragmentToShelfFragment()
            Navigation.findNavController(view).navigate(actionHomeToShelf)
        }
        view.buddyNavIV.setOnClickListener {
            val actionHomeToBuddyNav = HomeFragmentDirections.actionHomeFragmentToBuddyNavFragment()
            Navigation.findNavController(view).navigate(actionHomeToBuddyNav)
        }
        view.addBookIV.setOnClickListener {
            val actionHomeToScanner = HomeFragmentDirections.actionHomeFragmentToScannerFragment()
            Navigation.findNavController(view).navigate(actionHomeToScanner)
        }

        view.readingIV.setOnClickListener {
            val actionHomeToReadingList = HomeFragmentDirections.actionHomeFragmentToReadingFragment()
            Navigation.findNavController(view).navigate(actionHomeToReadingList)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragmentSettings = ToolbarFragmentSettings()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragmentSettings).commit()

    }
}