package de.hsrm.mi.mobilecomputing.bookworm.features.requests

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.ConfirmationType
import kotlinx.android.synthetic.main.fragment_friend_request.view.*

class FriendRequestFragment : Fragment() {
    private lateinit var viewModel: RequestViewModel
    val args: FriendRequestFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(RequestViewModel::class.java)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_friend_request, container, false)
        val userNameTV = view.findViewById<TextView>(R.id.friendRequestDetailTV)

        userNameTV.text = args.req.user.name

      view.acceptFriendIV.setOnClickListener {
            viewModel.acceptRequest(args.req)
          val actionFriendRequestToConfirmationFragment = FriendRequestFragmentDirections.actionFriendRequestFragmentToConfirmationFragment(args.req, ConfirmationType.ACCEPTED)
          view?.let { Navigation.findNavController(it).navigate(actionFriendRequestToConfirmationFragment) }
        }

       view.declineFriendIV.setOnClickListener {
            viewModel.declineRequest(args.req)
           val actionFriendRequestToConfirmationFragment = FriendRequestFragmentDirections.actionFriendRequestFragmentToConfirmationFragment(args.req, ConfirmationType.DECLINED)
           view?.let { Navigation.findNavController(it).navigate(actionFriendRequestToConfirmationFragment) }
        }
        return view
    }
}