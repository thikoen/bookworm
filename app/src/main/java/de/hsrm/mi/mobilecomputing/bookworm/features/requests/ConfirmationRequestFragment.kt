package de.hsrm.mi.mobilecomputing.bookworm.features.requests

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.ConfirmationType
import kotlinx.android.synthetic.main.fragment_confirmation_request.view.*
import kotlinx.android.synthetic.main.fragment_friend_request.view.*

class ConfirmationRequestFragment: Fragment() {
    private lateinit var viewModel: RequestViewModel
    val args: ConfirmationRequestFragmentArgs by navArgs()
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
        val view =  inflater.inflate(R.layout.fragment_confirmation_request, container, false)
        val confirmationText = view.findViewById<TextView>(R.id.confirmationText)
        var textFormatConfirmation = String()
        when {
            args.req.returnBook && args.confirmationType == ConfirmationType.ACCEPTED-> {
                textFormatConfirmation = "Great! ${args.req.user.name} returned your Book ${args.req.user.name} :)"
            }
            args.req.returnBook && args.confirmationType == ConfirmationType.DECLINED-> {
                textFormatConfirmation = "You declined the request from ${args.req.user.name} of returning your Book ${args.req.user.name}"
            }
            args.req.borrowBook && args.confirmationType == ConfirmationType.ACCEPTED-> {
                textFormatConfirmation = "Great! You lended ${args.req.user.name} your Book ${args.req.user.name} :)"
            }
            args.req.borrowBook && args.confirmationType == ConfirmationType.DECLINED-> {
                textFormatConfirmation = "You declined lending ${args.req.user.name} your Book ${args.req.user.name}"
            }
            !args.req.borrowBook && ! args.req.returnBook && args.confirmationType == ConfirmationType.ACCEPTED -> {
                textFormatConfirmation = "Great! You and ${args.req.user.name} are now buddys :)"
            }
            !args.req.borrowBook && ! args.req.returnBook && args.confirmationType == ConfirmationType.DECLINED -> {
                textFormatConfirmation = "You declined the friend request from ${args.req.user.name}"
            }
        }
        confirmationText.text = textFormatConfirmation


        view.confirmationButton.setOnClickListener {
            viewModel.acceptRequest(args.req)
            val actionConfirmationRequestToRequestList = ConfirmationRequestFragmentDirections.actionConfirmationFragmentToRequestListFragment()
            view?.let { Navigation.findNavController(it).navigate(actionConfirmationRequestToRequestList) }
        }

        return view
    }

}