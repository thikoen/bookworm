package de.hsrm.mi.mobilecomputing.bookworm.features.readinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.adapter.ReadingListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import kotlinx.android.synthetic.main.fragment_reading.view.*

class ReadingFragment : Fragment(){
    private lateinit var viewModel: ReadinglistViewModel
    private lateinit var readingListAdapter: ReadingListRecyclerAdapter
    private lateinit var wishToReadListAdapter: ReadingListRecyclerAdapter
    private lateinit var finishedListAdapter: ReadingListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(ReadinglistViewModel::class.java)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_reading, container, false)
        val noReadingTV = view.findViewById<TextView>(R.id.noReadingTV)
        val noWishToReadTV = view.findViewById<TextView>(R.id.noWishToReadTV)
        val noFinishedReadingTV = view.findViewById<TextView>(R.id.noFinishedReadingTV)

        view.addIcon.setOnClickListener {
            val actionReadingToShelf = ReadingFragmentDirections.actionReadingFragmentToShelfFragment()
            Navigation.findNavController(view).navigate(actionReadingToShelf)
        }
        view.addIcon1.setOnClickListener {
            val actionReadingToShelf = ReadingFragmentDirections.actionReadingFragmentToShelfFragment()
            Navigation.findNavController(view).navigate(actionReadingToShelf)
        }
        view.addIcon2.setOnClickListener {
            val actionReadingToShelf = ReadingFragmentDirections.actionReadingFragmentToShelfFragment()
            Navigation.findNavController(view).navigate(actionReadingToShelf)
        }

        val readingRV = view.findViewById<RecyclerView>(R.id.readingRV)
        val wishToReadRV = view.findViewById<RecyclerView>(R.id.wishToReadRV)
        val finishedReadingRV = view.findViewById<RecyclerView>(R.id.finishedReadingRV)

        val readingLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val wishToLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val finishedLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        readingRV.layoutManager = readingLayoutManager
        wishToReadRV.layoutManager = wishToLayoutManager
        finishedReadingRV.layoutManager = finishedLayoutManager

        readingListAdapter = ReadingListRecyclerAdapter(context) { partItem: Book -> partItemClicked(partItem) }
        readingRV.adapter = readingListAdapter
        if(viewModel.getReadingBooks().value != null)
            readingListAdapter.addAll(viewModel.getReadingBooks().value!!)
        viewModel.getReadingBooks().observe(viewLifecycleOwner, Observer { books ->
            if(viewModel.getReadingBooks().value == null || viewModel.getReadingBooks().value?.size!! > 0) {
                noReadingTV.visibility = View.INVISIBLE
                readingRV.visibility = View.VISIBLE
            }
            else{
                noReadingTV.visibility = View.VISIBLE
                readingRV.visibility = View.INVISIBLE
            }
            readingListAdapter.addAll(books)
        })

        wishToReadListAdapter = ReadingListRecyclerAdapter(context) { partItem: Book -> partItemClicked(partItem) }
        wishToReadRV.adapter = wishToReadListAdapter
        if(viewModel.getWishToReadBooks().value != null)
            wishToReadListAdapter.addAll(viewModel.getWishToReadBooks().value!!)
        viewModel.getWishToReadBooks().observe(viewLifecycleOwner, Observer { books ->
            if(viewModel.getWishToReadBooks().value == null || viewModel.getWishToReadBooks().value?.size!! > 0) {
                noWishToReadTV.visibility = View.INVISIBLE
                wishToReadRV.visibility = View.VISIBLE
            }
            else{
                noWishToReadTV.visibility = View.VISIBLE
                wishToReadRV.visibility = View.INVISIBLE
            }
            wishToReadListAdapter.addAll(books)
        })

        finishedListAdapter = ReadingListRecyclerAdapter(context) { partItem: Book -> partItemClicked(partItem) }
        finishedReadingRV.adapter = finishedListAdapter
        if(viewModel.getFinishedBooks().value != null)
            finishedListAdapter.addAll(viewModel.getFinishedBooks().value!!)
        viewModel.getFinishedBooks().observe(viewLifecycleOwner, Observer { books ->
            if(viewModel.getFinishedBooks().value == null || viewModel.getFinishedBooks().value?.size!! > 0) {
                noFinishedReadingTV.visibility = View.INVISIBLE
                finishedReadingRV.visibility = View.VISIBLE
            }
            else{
                noFinishedReadingTV.visibility = View.VISIBLE
                finishedReadingRV.visibility = View.INVISIBLE
            }
            finishedListAdapter.addAll(books)
        })

        return view
    }

    private fun partItemClicked(partItem: Book) {
        val actionReadingToSingleBook = ReadingFragmentDirections.actionReadingFragmentToSingleBookDetailsFragment(partItem)
        this.view?.let { Navigation.findNavController(it).navigate(actionReadingToSingleBook) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}