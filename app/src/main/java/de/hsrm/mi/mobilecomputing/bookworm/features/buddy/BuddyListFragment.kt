package de.hsrm.mi.mobilecomputing.bookworm.features.buddy

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.adapter.BuddyListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.User
import kotlinx.android.synthetic.main.fragment_buddy_list.view.*

class BuddyListFragment : Fragment() {
    lateinit var searchView: SearchView
    private lateinit var adapterBuddyList: BuddyListRecyclerAdapter
    private lateinit var viewModel: BuddyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(BuddyViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view =  inflater.inflate(R.layout.fragment_buddy_list, container, false)
        val noBuddyItems = view.findViewById<ConstraintLayout>(R.id.noBuddyItems)

        view.addButton.setOnClickListener {
            val actionBuddyListToAddFriend = BuddyListFragmentDirections.actionBuddyListFragmentToAddFriendFragment()
            Navigation.findNavController(view).navigate(actionBuddyListToAddFriend)
        }
        adapterBuddyList = BuddyListRecyclerAdapter(context) { partItem: User -> partItemClicked(partItem)}
        searchView = view.findViewById(R.id.searchViewBuddy)
        val buddyListRV = view.findViewById(R.id.buddyListRV) as RecyclerView

        val layoutManager = LinearLayoutManager(context)
        buddyListRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(buddyListRV.context, layoutManager.orientation)
        buddyListRV.addItemDecoration(dividerItemDecoration)

        if(viewModel.getBuddys().value != null)
            adapterBuddyList.addAll(viewModel.getBuddys().value!!)
        viewModel.getBuddys().observe(viewLifecycleOwner, Observer { buddys ->
            if(viewModel.getBuddys().value == null || viewModel.getBuddys().value?.size!! > 0) {
                noBuddyItems.visibility = View.GONE
                buddyListRV.visibility = View.VISIBLE
            }
            else{
                noBuddyItems.visibility = View.VISIBLE
                buddyListRV.visibility = View.GONE
            }
            adapterBuddyList.addAll(buddys)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (buddyListRV.toString().contains(query)) {
                    adapterBuddyList.filter.filter(query)
                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapterBuddyList.filter.filter(newText)
                return false
            }
        })

        buddyListRV.adapter = adapterBuddyList
        return view
    }

    private fun partItemClicked(partItem: User) {
        val actionBuddyListToBuddyShelf = BuddyListFragmentDirections.actionBuddyListFragmentToBuddyShelfFragment(partItem.UID, partItem.name.toString())
        this.view?.let { Navigation.findNavController(it).navigate(actionBuddyListToBuddyShelf) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }
}