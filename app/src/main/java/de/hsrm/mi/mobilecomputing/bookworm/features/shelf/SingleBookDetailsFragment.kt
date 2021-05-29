package de.hsrm.mi.mobilecomputing.bookworm.features.shelf

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.toolbar.ToolbarFragment
import kotlin.properties.Delegates


class SingleBookDetailsFragment : Fragment() {

    internal lateinit var bookAuthorTxt: TextView
    internal lateinit var bookTitleTxt: TextView
    internal lateinit var bookCoverIV: ImageView
    private val defaultImage : String = "https://images-na.ssl-images-amazon.com/images/I/618Duj4AwNL._AC_SL1500_.jpg"
    val args: SingleBookDetailsFragmentArgs by navArgs()
    private val viewModel = SingleBookViewModel()

    var bookDeleted : String by Delegates.observable("false") { _, oldValue, newValue ->
        onBookDeleted?.invoke(oldValue, newValue)
    }

    var onBookDeleted: ((String, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        val view =  inflater.inflate(R.layout.fragment_single_book_details, container, false)
        val readingChoice = resources.getStringArray(R.array.reading_choice)

        setSpinner(view)
        setBookAttributes(view)
        setLikeButtons(view)
        setDeleteButton(view)
        setRatingBar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarFragment = ToolbarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flToolbar, toolbarFragment).commit()
    }

    private fun setRatingBar(view : View){
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        //TODO
    }

    @SuppressLint("RestrictedApi")
    private fun setDeleteButton(view : View){
        val deleteButton = view.findViewById<ImageView>(R.id.deleteBookIV)
        deleteButton.setOnClickListener {
            basicAlert()
        }
    }

    private fun basicAlert(){
        val builder = AlertDialog.Builder(context)

        with(builder)
        {
            setTitle("Delete Book")
            setMessage("Do you want to delete book ${args.book.name}?")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Cancel", null)
            show()
        }
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        viewModel.deleteBook(args.book.isbn)
        bookDeleted = "true"
        findNavController().navigate(R.id.shelfFragment)
    }


    private fun setLikeButtons(view : View){
        val likeButton =  view.findViewById<ImageView>(R.id.likeIconIV)
        val dislikeButton =  view.findViewById<ImageView>(R.id.dislikeIconIV)

        likeButton.alpha = 0.3F
        dislikeButton.alpha = 0.3F
        if(args.book.liked) {
            likeButton.alpha = 1F
        } else if(args.book.disliked) {
            dislikeButton.alpha = 1F
        }

        likeButton.setOnClickListener {
            if(args.book.liked){
                args.book.liked = false
                likeButton.alpha = 0.3F
            } else{
                likeButton.alpha = 1F
                args.book.liked = true
            }
            dislikeButton.alpha = 0.3F
            args.book.disliked = false
            viewModel.setLikeDisliked(args.book)
        }

        dislikeButton.setOnClickListener {
            if(args.book.disliked){
                args.book.disliked = false
                likeButton.alpha = 0.3F
            } else{
                args.book.disliked = true
                dislikeButton.alpha = 1F
            }
            likeButton.alpha = 0.3F
            args.book.liked = false
            viewModel.setLikeDisliked(args.book)
        }
    }

    private fun setBookAttributes (view : View){
        val readingStatus_ = view.findViewById<TextView>(R.id.reading_status_TV)
        readingStatus_.text = "Currently ".plus(getBookStatus())

        bookAuthorTxt = view.findViewById(R.id.bookAuthorTV_BS)
        bookTitleTxt = view.findViewById(R.id.bookTitleTV_BS)
        bookCoverIV = view.findViewById(R.id.bookCoverIV_BS)
        bookCoverIV.setImageResource(R.drawable.clock)

        //Set Bookinfo
        bookTitleTxt.text = args.book.name
        bookAuthorTxt.text = args.book.author

        if(!args.book.thumbail.equals("empty", true) && !args.book.thumbail.equals("", true)){
            //Thumbnail from API found
            Picasso.get().load(args.book.thumbail?.replace("http", "https")).placeholder(R.drawable.book_small).into(
                bookCoverIV
            )
        } else {
            //No Thumbnail found
            Picasso.get().load(defaultImage).into(bookCoverIV)
        }
    }

    private fun setBookStatus(type: String){
        when {
            type.equals("Reading") -> {
                args.book.reading = true
                args.book.readSuccessfully = false
                args.book.wishToRead = false
            }
            type.equals("Wish to read") -> {
                args.book.reading = false
                args.book.readSuccessfully = false
                args.book.wishToRead = true
            }
            type.equals("Finished") -> {
                args.book.reading = false
                args.book.readSuccessfully = true
                args.book.wishToRead = false
            }
        }
        viewModel.writeReadingProperty(
            args.book.isbn,
            args.book.reading,
            args.book.wishToRead,
            args.book.readSuccessfully
        )
    }

    private fun getBookStatus () : String {
        when {
            args.book.reading -> {
                return "Reading"
            }
            args.book.wishToRead -> {
                return "Wish to read"
            }
            args.book.readSuccessfully -> {
                return "Finished"
            }
        }
        return ""
    }

    private fun setSpinner(view : View){
        val spinner = view.findViewById<Spinner>(R.id.avaliable_choicebox)
        var spinnerSelected : Boolean = false

        val adapter = ArrayAdapter.createFromResource(
            view.context, R.array.reading_choice,
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = adapter
        spinner?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                spinner.prompt = getBookStatus()
                if(spinnerSelected) {
                    setBookStatus(type)
                    spinnerSelected = false
                }
                else spinnerSelected = true
            }
        }
    }

}

