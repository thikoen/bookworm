package de.hsrm.mi.mobilecomputing.bookworm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.hsrm.mi.mobilecomputing.bookworm.features.borrowlist.BorrowingFragment
import de.hsrm.mi.mobilecomputing.bookworm.features.borrowlist.LendingFragment

class TabPagerAdapter(fm: FragmentManager, private var tabCount: Int) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return LendingFragment()
            1 -> return BorrowingFragment()
            else -> return LendingFragment()
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}