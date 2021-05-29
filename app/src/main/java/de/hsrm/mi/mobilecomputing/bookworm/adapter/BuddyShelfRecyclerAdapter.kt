package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import java.util.*
import kotlin.collections.ArrayList

class BuddyShelfRecyclerAdapter(internal var context: Context?, val clickListener: (Book) -> Unit) : RecyclerView.Adapter<BuddyShelfRecyclerAdapter.BuddyShelfRecyclerViewHolder>(), Filterable
{
    private var bookList: MutableList<Book>
    private var filteredData : MutableList<Book>
    private var mFilter: ItemFilter = ItemFilter()

    fun addAll(newBooks: List<Book>) {
        val init = bookList.size
        clear()
        bookList.addAll(newBooks)
        filteredData.addAll(newBooks)
        reload()
    }

    fun clear() {
        bookList.clear()
        filteredData.clear()
        reload()
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    init {
        this.filteredData = ArrayList()
        this.bookList = ArrayList()
    }

    inner class BuddyShelfRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bookTitle: TextView
        internal var bookAuthor: TextView
        internal var bookCover: ImageView
        internal var availableIcon: ImageView
        internal var likedIcon: ImageView
        internal var availableText: TextView

        init {
            bookTitle = itemView.findViewById(R.id.bookTitleTV_BS)
            bookAuthor = itemView.findViewById(R.id.bookAuthorTV_BS)
            bookCover = itemView.findViewById(R.id.bookCoverIV_BS)
            availableIcon = itemView.findViewById(R.id.availableIconIV)
            likedIcon = itemView.findViewById(R.id.likeIconIV)
            availableText = itemView.findViewById(R.id.availableTextTV)
        }

        fun bind(book: Book, clickListener: (Book) -> Unit) {
            bookTitle.text = book.name
            bookAuthor.text = book.author
            if(book.disliked) {
                likedIcon.setImageResource(R.drawable.dislike_icon)
            } else if(book.liked) {
                likedIcon.setImageResource(R.drawable.like_icon)
            } else {
                likedIcon.visibility = View.INVISIBLE
            }
            when {
                book.reading or book.borrowed -> {
                    availableIcon.setImageResource(R.drawable.occupied_icon)
                    availableText.setText(R.string.Occupied)
                }
                book.wishToRead -> {
                    availableIcon.setImageResource(R.drawable.reserved_icon)
                    availableText.setText(R.string.Reserved)
                }
                else -> {
                    availableIcon.setImageResource(R.drawable.free_icon)
                    availableText.setText(R.string.Free)
                }
            }

            if(!book.thumbail.equals("",true)){
                Picasso.get().load(book.thumbail.replace("http","https")).into(bookCover)
            }
            itemView.setOnClickListener{
                clickListener(book)
            }
        }
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterString = constraint.toString().toLowerCase()
            val results = FilterResults()
            val list: List<Book> = bookList
            val count = list.size
            val nlist = ArrayList<Book>()
            var checkName: String
            var checkAuthor: String
            for (i in 0 until count) {
                checkName = list[i].name.toLowerCase(Locale.ROOT)
                checkAuthor = list[i].author.toLowerCase(Locale.ROOT)
                if (checkName.contains(filterString) || checkAuthor.contains(filterString)) {
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
            if(results.count != 0) {
                filteredData = results.values as MutableList<Book>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuddyShelfRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_buddy_shelf, parent, false)
        return BuddyShelfRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    override fun onBindViewHolder(holderBuddyBookList: BuddyShelfRecyclerViewHolder, position: Int) {
        holderBuddyBookList.bind(filteredData[position], clickListener)

    }

    override fun getFilter(): Filter {
        return mFilter
    }
}