package de.hsrm.mi.mobilecomputing.bookworm.features.buddy

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import de.hsrm.mi.mobilecomputing.bookworm.adapter.BuddyListRecyclerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.model.User

class AddFriendFragment : Fragment() {
    private lateinit var adapter: BuddyListRecyclerAdapter
    lateinit var list: ArrayList<String>
    private lateinit var viewModel: BuddyViewModel
    private var clickedUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(BuddyViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view =  inflater.inflate(R.layout.fragment_add_friend, container, false)
        list = ArrayList()

        val searchView = view.findViewById<SearchView>(R.id.add_friend_searchView)
        val addFriendsRV = view.findViewById<RecyclerView>(R.id.addFriendsRV)
        searchView.setQuery("Insert ID....", false)

        val layoutManager = LinearLayoutManager(context)
        addFriendsRV.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(addFriendsRV.context, layoutManager.orientation)
        addFriendsRV.addItemDecoration(dividerItemDecoration)

        adapter = BuddyListRecyclerAdapter(context) { partItem: User -> partItemClicked(partItem)}
        addFriendsRV.adapter = adapter

        if(viewModel.getBuddys().value != null)
            adapter.addAll(viewModel.getBuddys().value!!)
        viewModel.getUsers().observe(viewLifecycleOwner, Observer { users ->
            adapter.addAll(users)
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (addFriendsRV.toString().contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    Toast.makeText(context, "No Match found", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return view
    }

    private fun partItemClicked(partItem: User) {
        clickedUser = partItem
        basicAlert()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }

    fun basicAlert(){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Friend Request")
            setMessage("Do you want to send a friend request to ${clickedUser.name}?")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", null)
            show()
        }
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        viewModel.sendFriendRequest(clickedUser)
    }

}