package de.hsrm.mi.mobilecomputing.bookworm.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.model.Request
import kotlin.collections.ArrayList

class RequestListRecyclerAdapter(internal var context: Context?, val clickListener: (Request) -> Unit) : RecyclerView.Adapter<RequestListRecyclerAdapter.RequestListViewHolder>() {
    private var requests: MutableList<Request>

    fun addAll(newRequests: List<Request>) {
        clear()
        requests.addAll(newRequests)
        reload()
    }

    fun clear() {
        requests.clear()
        reload()
    }

    private fun reload() {
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    init {
        this.requests = ArrayList()
    }

    inner class RequestListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var requestUserName: TextView
        internal var requestText: TextView

        init {
            requestUserName = itemView.findViewById(R.id.requestUserNameTV)
            requestText = itemView.findViewById(R.id.requestTextTV)
        }

        fun bind(request: Request, clickListener: (Request) -> Unit) {
            requestUserName.text = request.user.name
            when {
                request.book.isbn == "" -> {
                    val text = "User '${request.user.name}' wants to be your friend! :)"
                    requestText.text = text
                }
                request.returnBook -> {
                    val text = "User '${request.user.name}' wants to return your Book '${request.book.name}"
                    requestText.text = text
                }
                else -> {
                    val text = "User '${request.user.name}' wants to borrow your Book '${request.book.name}"
                    requestText.text = text
                }
            }

            if(!request.messageRead) {
                requestText.typeface = Typeface.DEFAULT_BOLD
                requestUserName.typeface = Typeface.DEFAULT_BOLD
            }

            itemView.setOnClickListener {
                clickListener(request)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestListViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.single_item_request_list, parent, false)
        return RequestListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(
        holderBuddyBookList: RequestListViewHolder,
        position: Int
    ) {
        holderBuddyBookList.bind(requests[position], clickListener)
    }
}