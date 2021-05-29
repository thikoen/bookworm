package de.hsrm.mi.mobilecomputing.bookworm.features.scanner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.google.zxing.integration.android.IntentIntegrator
import de.hsrm.mi.mobilecomputing.bookworm.MainActivity
import de.hsrm.mi.mobilecomputing.bookworm.R
import de.hsrm.mi.mobilecomputing.bookworm.features.shelf.ShelfDAO
import de.hsrm.mi.mobilecomputing.bookworm.model.Book
import de.hsrm.mi.mobilecomputing.bookworm.model.FirebaseRepo
import de.hsrm.mi.mobilecomputing.bookworm.model.Shelf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.properties.Delegates.observable


class ScannerFragment : Fragment() {
    private val firebaseRepo = FirebaseRepo()
    var isbnCode : String by observable("none") { _, oldValue, newValue ->
        onTitleChanged?.invoke(oldValue, newValue)
    }
    var onTitleChanged: ((String, String) -> Unit)? = null

    var aborted : String by observable("false") { _, oldValue, newValue ->
        onAbortedChanged?.invoke(oldValue, newValue)
    }
    var onAbortedChanged: ((String, String) -> Unit)? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_scanner, container, false)

        val integrator : IntentIntegrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(false)
        integrator.setPrompt("Scan Barcode")
        integrator.setBeepEnabled(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.initiateScan()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            aborted = "true"
        }
        callback.isEnabled = true
        onAbortedChanged = { oldValue, newValue ->
            Toast.makeText(
                    context,
                    "Aborted Scan.",
                    Toast.LENGTH_SHORT
            ).show()
            val homeIntent = Intent(context, MainActivity::class.java)
            startActivity(homeIntent)
        }

        onTitleChanged = { oldValue, newValue ->
            val scannedBook = Book("", "", isbnCode)
                //The Book need to be added into the shelf und pushed into Firebase
            val shelfDAO = ShelfDAO(firebaseRepo.getUser().toString())
            val shelf = Shelf(listOf(scannedBook))

                GlobalScope.launch {
                    delay(1000)
                    shelfDAO.shelfToStorage(shelf)
                }

            testForInvalidBookDataFromISBN("https://www.googleapis.com/books/v1/volumes?q=isbn:${isbnCode}")

            val actionScannerToScanCompleted = ScannerFragmentDirections.actionScannerFragmentToScanCompletedFragment(scannedBook)
            Navigation.findNavController(view).navigate(actionScannerToScanCompleted)

        }
        return view
    }

    private fun testForInvalidBookDataFromISBN(url: String){
        var responseAsJSON = JSONObject()
        Fuel.get(url)
                .responseJson { request, response, result ->
                    responseAsJSON = JSONObject(result.get().content)
                    try {
                        val name = responseAsJSON
                                .getJSONArray("items")
                                .getJSONObject(0)
                                .getJSONObject("volumeInfo")
                                .get("title")
                                .toString()
                    } catch (e: Exception){
                        Toast.makeText(
                                context,
                                "Could not find your desired book in Google Books, sorry!",
                                Toast.LENGTH_LONG
                        ).show()
                        Toast.makeText(
                                context,
                                "Book added with >Unknown< in your shelf.",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //retrieve the scan result
        val scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (scanningResult.contents == null) {
            super.onActivityResult(requestCode, resultCode, intent)
            aborted = "true"
            return
        }
        isbnCode = scanningResult.contents
    }
}