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

class ShelfRecyclerAdapter(internal var context: Context?, val clickListener: (Book) -> Unit) : RecyclerView.Adapter<ShelfRecyclerAdapter.ShelfRecyclerViewHolder>(), Filterable
{
    internal var bookList: MutableList<Book>
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
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    init {
        this.filteredData = ArrayList()
        this.bookList = ArrayList()
    }


    inner class ShelfRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var bookAuthorTxt: TextView
        internal var bookTitleTxt: TextView
        internal var bookCoverIV: ImageView

        init {
            bookAuthorTxt = itemView.findViewById(R.id.bookAuthorTV_BS)
            bookTitleTxt = itemView.findViewById(R.id.bookTitleTV_BS)
            bookCoverIV = itemView.findViewById(R.id.bookCoverIV_BS)
        }


        fun bind(book: Book, clickListener: (Book) -> Unit) {
            bookTitleTxt.text = book.name
            bookAuthorTxt.text = book.author
            if(!book.thumbail.equals("",true) && !book.thumbail.equals("empty",true)){
                Picasso.get().load(book.thumbail.replace("http","https")).into(bookCoverIV)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfRecyclerViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.single_item_shelf, parent, false)
        return ShelfRecyclerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }
    override fun getFilter(): Filter {
        return mFilter
    }

    override fun onBindViewHolder(holderShelf: ShelfRecyclerViewHolder, position: Int) {
        holderShelf.bind(filteredData[position], clickListener)
    }

}