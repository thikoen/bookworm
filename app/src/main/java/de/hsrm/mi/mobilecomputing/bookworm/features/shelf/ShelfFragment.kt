package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.adapter.ShelfRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo

class ShelfFragment : Fragment() {
    private lateinit var adapterShelf: ShelfRecyclerAdapter
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
        val view =  inflater.inflate(R.layout.fragment_shelf, container, false)
        val searchView = view.findViewById<SearchView>(R.id.searchViewShelf)
        val gridLayoutManager = GridLayoutManager(context, 2)
        val shelfGV = view.findViewById<RecyclerView>(R.id.shelfGV)
        val noShelfItems = view.findViewById<ConstraintLayout>(R.id.noShelfItems)
        shelfGV.layoutManager = gridLayoutManager
        val dividerItemDecoration = DividerItemDecoration(shelfGV.context, gridLayoutManager.orientation)
        shelfGV.addItemDecoration(dividerItemDecoration)
        findNavController().popBackStack(R.id.singleBookDetailsFragment, true)
        adapterShelf = ShelfRecyclerAdapter(context) { partItem: Book -> partItemClicked(partItem) }
        if(viewModel.getBooks().value != null){
            adapterShelf.addAll(viewModel.getBooks().value!!)
        }
        viewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            adapterShelf.addAll(books)
            if(viewModel.getBooks().value == null || viewModel.getBooks().value?.size!! > 0) {
                noShelfItems.visibility = View.GONE
                shelfGV.visibility = View.VISIBLE
            }
            else{
                noShelfItems.visibility = View.VISIBLE
                shelfGV.visibility = View.GONE
            }
        })
        shelfGV.adapter = adapterShelf
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (shelfGV.toString().contains(query)) {
                    adapterShelf.filter.filter(query)
                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapterShelf.filter.filter(newText)
                return false
            }
        })
        return view
    }

    private fun partItemClicked(partItem: Book) {
        val actionShelfToSingleBook = ShelfFragmentDirections.actionShelfFragmentToSingleBookDetailsFragment(partItem)
        this.view?.let { Navigation.findNavController(it).navigate(actionShelfToSingleBook) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}


