package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Lending

class LendingListRecyclerAdapter(internal var context: Context?, private val clickListener: (Lending) -> Unit) : RecyclerView.Adapter<LendingListRecyclerAdapter.LendingListRecyclerViewHolder>()
{
    private var lendingList: MutableList<Lending> = ArrayList()

    fun addAll(newLending: List<Lending>) {
        clear()
        lendingList.addAll(newLending)
        reload()
    }

    fun clear() {
        lendingList.clear()
        reload()
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    inner class LendingListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bookTitle: TextView
        internal var userName: TextView
        internal var bookAuthor: TextView
        internal var lendingDate: TextView
        internal var bookButton: Button

        init {
            bookTitle = itemView.findViewById(R.id.lendingBookTitle)
            userName = itemView.findViewById(R.id.requestUserNameTV)
            bookAuthor = itemView.findViewById(R.id.lendingBookAuthor)
            lendingDate = itemView.findViewById(R.id.lending_date)
            bookButton = itemView.findViewById(R.id.bookButton)
        }

        fun bind(lending: Lending, clickListener: (Lending) -> Unit) {
            bookTitle.text = lending.bookTitle
            userName.text = lending.userName
            bookAuthor.text = lending.bookAuthor
            lendingDate.text = lending.date
            bookButton.setOnClickListener{
                clickListener(lending)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LendingListRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_lending, parent, false)
        return LendingListRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lendingList.size
    }

    override fun onBindViewHolder(holderLendingList: LendingListRecyclerViewHolder, position: Int) {
        holderLendingList.bind(lendingList[position], clickListener)
    }
}