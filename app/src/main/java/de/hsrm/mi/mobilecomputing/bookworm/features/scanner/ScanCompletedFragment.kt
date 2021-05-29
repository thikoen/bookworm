package de.hsrm.mi.mobilecomputing.bookworm.features.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import de.hsrm.mi.mobilecomputing.bookworm.R

class ScanCompletedFragment : Fragment() {
    val args: ScanCompletedFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_scan_completed, container, false)

        view.findViewById<ImageView>(R.id.moreIV).setOnClickListener {
            val actionScanCompletedToScanner = ScanCompletedFragmentDirections.actionScanCompletedFragmentToScannerFragment()
            Navigation.findNavController(view).navigate(actionScanCompletedToScanner)
        }
        view.findViewById<ImageView>(R.id.showIV).setOnClickListener {
            val actionScanCompletedToSingleBook = ScanCompletedFragmentDirections.actionScanCompletedFragmentToSingleBookDetailsFragment(args.book)
            Navigation.findNavController(view).navigate(actionScanCompletedToSingleBook)
        }
        return view
    }
}