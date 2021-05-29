package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.adapter.BuddyShelfRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import java.lang.invoke.ConstantCallSite

class BuddyShelfFragment: Fragment() {
    private lateinit var adapterBuddyShelf: BuddyShelfRecyclerAdapter
    val args: BuddyShelfFragmentArgs by navArgs()
    val firebaseRepo = FirebaseRepo()
    private var clickedBook = Book()
    private lateinit var viewModel: ShelfViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(ShelfViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_buddy_shelf, container, false)
        val buddyName = view.findViewById<TextView>(R.id.userNameTV)
        val searchView = view.findViewById<SearchView>(R.id.searchViewBuddyShelf)
        val noBooksTV = view.findViewById<TextView>(R.id.noBooksInBuddyShelfTV)
        buddyName.setText(args.Name)
        val layoutManager = GridLayoutManager(context, 2)
        val buddyShelfGV = view.findViewById<RecyclerView>(R.id.buddyShelfGV)
        buddyShelfGV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(buddyShelfGV.context, layoutManager.orientation)
        buddyShelfGV.addItemDecoration(dividerItemDecoration)

        adapterBuddyShelf = BuddyShelfRecyclerAdapter(context) { partItem: Book -> partItemClicked(partItem) }
        viewModel.setBuddyUID(args.UID)
        if(viewModel.getBuddyBooks().value != null)
            adapterBuddyShelf.addAll(viewModel.getBuddyBooks().value!!)
        viewModel.getBuddyBooks().observe(viewLifecycleOwner, Observer { books ->
            if(viewModel.getBuddyBooks().value == null || viewModel.getBuddyBooks().value?.size!! > 0) {
                noBooksTV.visibility = View.GONE
                buddyShelfGV.visibility = View.VISIBLE
            }
            else{
                noBooksTV.visibility = View.VISIBLE
                buddyShelfGV.visibility = View.GONE
            }
            adapterBuddyShelf.addAll(books)
        })

        buddyShelfGV.adapter = adapterBuddyShelf
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (buddyShelfGV.toString().contains(query)) {
                    adapterBuddyShelf.filter.filter(query)
                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapterBuddyShelf.filter.filter(newText)
                return false
            }
        })
        return view
    }

    private fun partItemClicked(partItem: Book) {
        clickedBook = partItem
        when {
            clickedBook.borrowed or clickedBook.reading ->
                occupiedAlert()
            clickedBook.wishToRead ->
                reservedAlert()
            else ->
                freeAlert()
        }
    }

    fun freeAlert(){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Book Request")
            setMessage("Do you want to borrow ${clickedBook.name}?")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", null)
            show()
        }
    }

    fun reservedAlert(){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Book Request")
            setMessage("${clickedBook.name} is reserved at this moment, but you can still send a request.")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", null)
            show()
        }
    }

    fun occupiedAlert(){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Sorry!")
            setMessage("${clickedBook.name} is occupied at this moment.")
            setPositiveButton("OK", null)
            show()
        }
    }

    fun confirmAlert(){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Request sended")
            setMessage("User ${args.Name} has been notified.")
            setPositiveButton("OK", null)
            show()
        }
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        viewModel.sendBookBorrowRequest(args.UID, firebaseRepo.getUsername(), clickedBook)
        confirmAlert()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}