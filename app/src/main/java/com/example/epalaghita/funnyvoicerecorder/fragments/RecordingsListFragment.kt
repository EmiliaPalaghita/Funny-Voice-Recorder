package com.example.epalaghita.funnyvoicerecorder.fragments

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.epalaghita.funnyvoicerecorder.R
import com.example.epalaghita.funnyvoicerecorder.adapters.RecordsAdapter
import com.example.epalaghita.funnyvoicerecorder.models.Record
import com.example.epalaghita.funnyvoicerecorder.utils.RecordPlayer
import kotlinx.android.synthetic.main.recordings_layout.*
import java.io.File

class RecordingsListFragment : Fragment() {

    private var records: ArrayList<Record> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recordings_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO - update recycler view every time a new recording is created
        addFiles()

        records_recycler_view.layoutManager = LinearLayoutManager(this.context)

        records_recycler_view.adapter = this.context?.let { RecordsAdapter(records, it) }
    }

    private fun addFiles() {
        val audioDirectory = File(RecordPlayer.PATH)
        if (!audioDirectory.exists())
            return

        val files = audioDirectory.listFiles()

        files.forEach { records.add(Record(it)) }

    }
}