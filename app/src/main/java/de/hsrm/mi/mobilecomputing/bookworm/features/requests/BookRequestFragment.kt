package de.hsrm.mi.mobilecomputing.bookworm.features.requests

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.ConfirmationType
import kotlinx.android.synthetic.main.fragment_book_request.view.*

class BookRequestFragment: Fragment() {
    private lateinit var viewModel: RequestViewModel
    val args: BookRequestFragmentArgs by navArgs()
    var book = Book()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(RequestViewModel::class.java)
        viewModel.getSingleBook(args.req.book.isbn)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_book_request, container, false)
        val userTV = view.findViewById<TextView>(R.id.userNameRequestDetailTV)
        val bookTitle = view.findViewById<TextView>(R.id.bookTitleRequest)
        val bookAuthor = view.findViewById<TextView>(R.id.bookAuthorRequestTV)
        val bookImage = view.findViewById<ImageView>(R.id.bookRequestIV)
        val wantsTo = view.findViewById<TextView>(R.id.wantsToTV)

        bookTitle.text = args.req.book.name
        userTV.text = args.req.user.name
        bookAuthor.text = args.req.book.author
        if(args.req.returnBook) {
            wantsTo.text = "wants to return:"
            bookImage.setImageResource(R.drawable.return_icon)
        } else{
            wantsTo.text = "wants to borrow:"
            bookImage.setImageResource(R.drawable.borrow_icon)
        }

        view.toBookButtonRequest.setOnClickListener {
            val actionBookRequestToSingleBook = BookRequestFragmentDirections.actionBookRequestFragmentToSingleBookDetailsFragment(viewModel.getSingleBook())
            view?.let { Navigation.findNavController(it).navigate(actionBookRequestToSingleBook) }
        }

        view.acceptBookIV.setOnClickListener {
            viewModel.acceptRequest(args.req)
            val actionBookRequestToConfirmationFragment = BookRequestFragmentDirections.actionBookRequestFragmentToConfirmationFragment(args.req, ConfirmationType.ACCEPTED)
            view?.let { Navigation.findNavController(it).navigate(actionBookRequestToConfirmationFragment) }
        }

        view.declineBookIV.setOnClickListener {
            viewModel.declineRequest(args.req)
            val actionBookRequestToConfirmationFragment = BookRequestFragmentDirections.actionBookRequestFragmentToConfirmationFragment(args.req, ConfirmationType.DECLINED)
            view?.let { Navigation.findNavController(it).navigate(actionBookRequestToConfirmationFragment) }
        }

        return view
    }


}