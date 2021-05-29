package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.User
import java.util.*
import kotlin.collections.ArrayList

class BuddyListRecyclerAdapter(internal var context: Context?, val clickListener: (User) -> Unit) : RecyclerView.Adapter<BuddyListRecyclerAdapter.BuddyListRecyclerViewHolder>(), Filterable
{
    private var buddyList: MutableList<User>
    private var filteredData : MutableList<User>
    private var mFilter: ItemFilter = ItemFilter()

    fun addAll(newUsers: List<User>) {
        val init = buddyList.size
        clear()
        buddyList.addAll(newUsers)
        filteredData.addAll(newUsers)
        reload()
    }

    fun clear() {
        buddyList.clear()
        filteredData.clear()
        reload()
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    init {
        this.filteredData = ArrayList()
        this.buddyList = ArrayList()
    }

    inner class BuddyListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var buddyName: TextView
        internal var buddyID: TextView

        init {
            buddyName = itemView.findViewById(R.id.buddyNameTV)
            buddyID = itemView.findViewById(R.id.buddyIdTV)
        }

        fun bind(user: User, clickListener: (User) -> Unit) {
            buddyName.text = user.name
            buddyID.text = user.id.toString()
            itemView.setOnClickListener{
                clickListener(user)
            }
        }
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterString = constraint.toString().toLowerCase()
            val results = FilterResults()
            val list: List<User> = buddyList
            val count = list.size
            val nlist = ArrayList<User>()
            var checkName: String
            var checkID: String
            for (i in 0 until count) {
                checkID = list[i].id.toString()
                checkName = list[i].name?.toLowerCase(Locale.ROOT).toString()
                if (checkName.contains(filterString) || checkID.contains(filterString)) {
                    nlist.add(list[i])
                }
            }
            results.values = nlist
            results.count = nlist.size
            return results
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            if(results.count != 0){
                filteredData = results.values as MutableList<User>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuddyListRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_buddy, parent, false)
        return BuddyListRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    override fun onBindViewHolder(holderBuddyList: BuddyListRecyclerViewHolder, position: Int) {
        holderBuddyList.bind(filteredData[position], clickListener)
    }

    override fun getFilter(): Filter {
        return mFilter
    }
}