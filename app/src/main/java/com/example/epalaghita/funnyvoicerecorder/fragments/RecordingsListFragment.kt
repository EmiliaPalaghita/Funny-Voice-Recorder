package com.example.epalaghita.funnyvoicerecorder.fragments

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.epalaghita.funnyvoicerecorder.R
import com.example.epalaghita.funnyvoicerecorder.adapters.RecordsAdapter
import com.example.epalaghita.funnyvoicerecorder.models.Record
import kotlinx.android.synthetic.main.recordings_layout.*
import java.io.File

class RecordingsListFragment : Fragment() {

    private var records: ArrayList<Record> = ArrayList()
    private val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recordings_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addFiles()

        records_recycler_view.layoutManager = LinearLayoutManager(this.context)


        records_recycler_view.adapter = this.context?.let { RecordsAdapter(records, it) }
    }

    private fun addFiles() {
        val audioDirectory = File(PATH)
        if (!audioDirectory.exists())
            return

        val files = audioDirectory.listFiles()

        files.forEach {records.add(Record(it)) }

    }
}