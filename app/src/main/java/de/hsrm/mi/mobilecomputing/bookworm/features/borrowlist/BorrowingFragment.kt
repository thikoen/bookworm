package de.hsrm.mi.mobilecomputing.bookworm.features.borrowlist

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.adapter.BorrowingListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.Lending

class BorrowingFragment : Fragment() {
    private lateinit var adapterBorrowing: BorrowingListRecyclerAdapter
    private lateinit var viewModel: LendingViewModel
    private var returnBook = Lending()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(LendingViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view =  inflater.inflate(R.layout.fragment_borrowing, container, false)

        adapterBorrowing = BorrowingListRecyclerAdapter(context) { returnItem: Lending -> returnButtonClicked(returnItem)}

        val borrowingRV = view.findViewById(R.id.borrowingRV) as RecyclerView

        val layoutManager = LinearLayoutManager(context)
        borrowingRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(borrowingRV.context, layoutManager.orientation)
        borrowingRV.addItemDecoration(dividerItemDecoration)

        if(viewModel.getLendingList().value != null)
            adapterBorrowing.addAll(viewModel.getBorrowingList().value!!)
        viewModel.getBorrowingList().observe(viewLifecycleOwner, Observer { lending ->
            adapterBorrowing.addAll(lending)
        })

        borrowingRV.adapter = adapterBorrowing
        return view
    }

    private fun returnButtonClicked(partItem: Lending) {
        returnBook = partItem
        basicAlert()
    }

    fun basicAlert(){
        val builder = AlertDialog.Builder(context)

        with(builder)
        {
            setTitle("Return Book")
            setMessage("Do you want to return book ${returnBook.bookTitle} to ${returnBook.userName}?")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", null)
            show()
        }
    }

    fun confirmAlert(){
        val builder = AlertDialog.Builder(context)

        with(builder)
        {
            setTitle("Request sended")
            setMessage("User ${returnBook.userName} has been notified.")
            setPositiveButton("OK", null)
            show()
        }
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        viewModel.sendReturnRequest(returnBook)
        confirmAlert()
    }
}