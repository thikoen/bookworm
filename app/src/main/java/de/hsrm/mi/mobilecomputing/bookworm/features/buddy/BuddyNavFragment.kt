package de.hsrm.mi.mobilecomputing.bookworm.features.buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.features.requests.RequestViewModel
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import kotlinx.android.synthetic.main.fragment_buddy_nav.view.*

class BuddyNavFragment : Fragment() {
    private lateinit var viewModel: RequestViewModel
    val firebaseRepo = FirebaseRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(RequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_buddy_nav, container, false)
        view.buddyIV.setOnClickListener{
            val actionBuddyNavToBuddyList = BuddyNavFragmentDirections.actionBuddyNavFragmentToBuddyListFragment()
            Navigation.findNavController(view).navigate(actionBuddyNavToBuddyList)
        }

        view.requestIV.setOnClickListener{
            val actionBuddyNavToRequestList = BuddyNavFragmentDirections.actionBuddyNavFragmentToRequestListFragment()
            Navigation.findNavController(view).navigate(actionBuddyNavToRequestList)
        }
        view.borrowListIV.setOnClickListener{
            val actionBuddyNavToBorrowList = BuddyNavFragmentDirections.actionBuddyNavFragmentToBorrowListFragment()
            Navigation.findNavController(view).navigate(actionBuddyNavToBorrowList)
        }

        val bookAlert = view.findViewById<ImageView>(R.id.alertRequestIV)

        val unseenReq = Observer<Boolean> { unseen ->
            if(unseen){
                bookAlert.visibility = View.VISIBLE
            }else{
                bookAlert.visibility = View.INVISIBLE
            }
        }
        viewModel.unseenRequests.observe(viewLifecycleOwner, unseenReq)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}