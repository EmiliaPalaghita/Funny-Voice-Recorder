package com.example.epalaghita.funnyvoicerecorder.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.epalaghita.funnyvoicerecorder.fragments.RecordFragment
import com.example.epalaghita.funnyvoicerecorder.fragments.RecordingsListFragment

class ViewPageAdapter(manager : FragmentManager) : FragmentPagerAdapter(manager) {

    private val TAB_NUMBER = 2

    override fun getItem(p0: Int): Fragment {
        return when(p0) {
            0 -> RecordFragment()
            else -> RecordingsListFragment()
        }
    }

    override fun getCount(): Int = TAB_NUMBER

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Record"
            else -> "All recordings"
        }
    }
}