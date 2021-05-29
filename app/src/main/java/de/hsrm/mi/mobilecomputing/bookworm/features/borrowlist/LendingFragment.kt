package de.hsrm.mi.mobilecomputing.bookworm.features.borrowlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.adapter.LendingListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.features.shelf.ShelfViewModel
import de.hsrm.mi.mobilecomputing.bookworm.features.shelf.SingleBookViewModel
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.model.Lending

class LendingFragment : Fragment() {
    private lateinit var adapterLending: LendingListRecyclerAdapter
    private lateinit var viewModel: LendingViewModel
    private lateinit var shelfViewModel: ShelfViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(LendingViewModel::class.java)
        shelfViewModel = ViewModelProviders.of(requireActivity()).get(ShelfViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view =  inflater.inflate(R.layout.fragment_lending, container, false)

        shelfViewModel.loadBooks()
        adapterLending = LendingListRecyclerAdapter(context) { partItem: Lending -> partItemClicked(partItem)}

        val lendingRV = view.findViewById(R.id.lendingRV) as RecyclerView

        val layoutManager = LinearLayoutManager(context)
        lendingRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(lendingRV.context, layoutManager.orientation)
        lendingRV.addItemDecoration(dividerItemDecoration)

        if(viewModel.getLendingList().value != null)
            adapterLending.addAll(viewModel.getLendingList().value!!)
        viewModel.getLendingList().observe(viewLifecycleOwner, Observer { lending ->
            adapterLending.addAll(lending)
        })

        lendingRV.adapter = adapterLending
        return view
    }

    private fun partItemClicked(partItem: Lending) {
        val book = shelfViewModel.getBookWithISBN(partItem.isbn)
        val actionLendingToBook = BorrowListFragmentDirections.actionBorrowListFragmentToSingleBookDetailsFragment(book)
        this.view?.let { Navigation.findNavController(it).navigate(actionLendingToBook) }
    }
}