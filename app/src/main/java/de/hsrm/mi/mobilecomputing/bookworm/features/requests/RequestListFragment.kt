package de.hsrm.mi.mobilecomputing.bookworm.features.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.adapter.RequestListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.Request

class RequestListFragment: Fragment() {
    private lateinit var requestListRecyclerAdapter: RequestListRecyclerAdapter
    private lateinit var viewModel: RequestViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(RequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_request_list, container, false)
        val layoutManager = LinearLayoutManager(context)
        val noRequests = view.findViewById<ConstraintLayout>(R.id.noRequestItems)
        val requestListRV = view.findViewById<RecyclerView>(R.id.requestListRV)
        requestListRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(requestListRV.context, layoutManager.orientation)
        requestListRV.addItemDecoration(dividerItemDecoration)
        findNavController().popBackStack(R.id.bookRequestFragment, true)
        findNavController().popBackStack(R.id.friendRequestFragment, true)
        findNavController().popBackStack(R.id.confirmationFragment, true)

        requestListRecyclerAdapter = RequestListRecyclerAdapter(context) { partItem: Request -> partItemClicked(partItem) }
        requestListRV.adapter = requestListRecyclerAdapter
        // val model: ShelfViewModel by viewModels()
        if(viewModel.getRequests().value != null)
            requestListRecyclerAdapter.addAll(viewModel.getRequests().value!!)
        viewModel.getRequests().observe(viewLifecycleOwner, Observer { requests ->
            if(viewModel.getRequests().value == null || viewModel.getRequests().value?.size!! > 0) {
                noRequests.visibility = View.GONE
                requestListRV.visibility = View.VISIBLE
            }
            else{
                noRequests.visibility = View.VISIBLE
                requestListRV.visibility = View.GONE
            }
            requestListRecyclerAdapter.addAll(requests)
        })

        return view
    }

    private fun partItemClicked(partItem: Request) {
        if(!partItem.messageRead) {
            viewModel.markRequestAsRead(partItem)
        }
        when {
            partItem.borrowBook || partItem.returnBook -> {
                val actionRequestListToBorrowingBookRequestFragment = RequestListFragmentDirections.actionRequestListFragmentToBookRequestFragment( partItem)
                view?.let { Navigation.findNavController(it).navigate(actionRequestListToBorrowingBookRequestFragment) }
            }
            else -> {
                val actionRequestListToFriendRequestFragment = RequestListFragmentDirections.actionRequestListFragmentToFriendRequestFragment(partItem)
                view?.let { Navigation.findNavController(it).navigate(actionRequestListToFriendRequestFragment) }
            }
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}