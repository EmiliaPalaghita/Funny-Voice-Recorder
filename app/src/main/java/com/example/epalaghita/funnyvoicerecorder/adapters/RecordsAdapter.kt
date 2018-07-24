package com.example.epalaghita.funnyvoicerecorder.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.epalaghita.funnyvoicerecorder.R
import com.example.epalaghita.funnyvoicerecorder.models.Record
import com.example.epalaghita.funnyvoicerecorder.utils.RecordCallback
import com.example.epalaghita.funnyvoicerecorder.utils.RecordPlayer
import kotlinx.android.synthetic.main.record_list_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RecordsAdapter(val items: ArrayList<Record>, val context: Context) : RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.record_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordName = items[position].name.substring(0, items[position].name.length - 4).toLong()
        val format: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        val inputDate: String = format.format(recordName)

        holder.bind(inputDate, items[position].name)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        private val tvRecord: TextView = view.record_textView
        private val btnPlay: Button = view.play_record_button
        private var recordName: String? = null

        init {
            btnPlay.setOnClickListener{playRecord()}
        }

        fun bind(message: String, recordName: String) {
            tvRecord.text = message
            this.recordName = recordName
        }

        private fun playRecord() {
             btnPlay.setBackgroundResource(R.drawable.microphone_record)

             val path = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/" + recordName

             RecordPlayer.playRecord(path, object : RecordCallback {
                 override fun onMediaPlayerFinished() {
                     // do the magic
                     btnPlay.setBackgroundResource(R.drawable.microphone)

                 }
             })
        }
    }
}