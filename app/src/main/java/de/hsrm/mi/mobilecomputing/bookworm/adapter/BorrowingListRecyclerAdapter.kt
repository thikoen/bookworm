package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Lending

class BorrowingListRecyclerAdapter(internal var context: Context?, private val clickListener: (Lending) -> Unit) : RecyclerView.Adapter<BorrowingListRecyclerAdapter.BorrowingListRecyclerViewHolder>()
{
    private var lendingList: MutableList<Lending> = ArrayList()

    fun addAll(newLending: List<Lending>) {
        val init = lendingList.size
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

    inner class BorrowingListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bookTitle: TextView
        internal var userName: TextView
        internal var bookAuthor: TextView
        internal var borrowedDate: TextView
        internal var returnBookButton: ImageView

        init {
            bookTitle = itemView.findViewById(R.id.borrowingBookTitle)
            userName = itemView.findViewById(R.id.requestUserNameBorrowingTV)
            bookAuthor = itemView.findViewById(R.id.borrowingBookAuthor)
            borrowedDate = itemView.findViewById(R.id.borrowedSinceTV)
            returnBookButton = itemView.findViewById(R.id.returnIconIV)
        }

        fun bind(lending: Lending, clickListener: (Lending) -> Unit) {
            bookTitle.text = lending.bookTitle
            userName.text = lending.userName
            bookAuthor.text = lending.bookAuthor
            borrowedDate.text = lending.date
            returnBookButton.setOnClickListener{
                clickListener(lending)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowingListRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_borrowing, parent, false)
        return BorrowingListRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lendingList.size
    }

    override fun onBindViewHolder(holderLendingList: BorrowingListRecyclerViewHolder, position: Int) {
        holderLendingList.bind(lendingList[position], clickListener)
    }
}