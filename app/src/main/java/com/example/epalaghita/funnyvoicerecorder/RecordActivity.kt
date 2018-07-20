package com.example.epalaghita.funnyvoicerecorder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.epalaghita.funnyvoicerecorder.adapters.ViewPageAdapter
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {

    private lateinit var pageAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        pageAdapter = ViewPageAdapter(supportFragmentManager)
        viewpager.adapter = pageAdapter
        tabs.setupWithViewPager(viewpager)

        /*It's not working and I have no idea why*/
        val tabStrip: LinearLayout = tabs.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener { _, _ -> true }
        }

    }


}
