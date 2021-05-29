package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import kotlin.collections.ArrayList


class ReadingListRecyclerAdapter(internal var context: Context?, val clickListener: (Book) -> Unit) : RecyclerView.Adapter<ReadingListRecyclerAdapter.ReadingListRecyclerViewHolder>()
{
    internal var bookList: MutableList<Book>

    fun addAll(newBooks: List<Book>) {
        clear()
        bookList.addAll(newBooks)
        reload()
    }

    fun clear() {
        bookList.clear()
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    init {
        this.bookList = ArrayList()
    }


    inner class ReadingListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bookTitleTxt: TextView
        internal var bookCoverIV: ImageView

        init {
            bookTitleTxt = itemView.findViewById(R.id.bookTitleReadingTV)
            bookCoverIV = itemView.findViewById(R.id.bookCoverReadingIV)
        }


        fun bind(book: Book, clickListener: (Book) -> Unit) {
            bookTitleTxt.text = book.name
            if(!book.thumbail.equals("",true)){
                Picasso.get().load(book.thumbail.replace("http","https")).into(bookCoverIV)
            }
            itemView.setOnClickListener{
                clickListener(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingListRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_reading, parent, false)
        return ReadingListRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holderShelf: ReadingListRecyclerViewHolder, position: Int) {
        holderShelf.bind(bookList[position], clickListener)
    }
}