package com.example.epalaghita.funnyvoicerecorder.fragments

import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.epalaghita.funnyvoicerecorder.R
import android.content.pm.PackageManager

class RecordFragment : Fragment() {

    var isRecording = false
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    lateinit var mMediaRecorder: MediaRecorder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.record_fragment, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()

    }

    fun record() {
        //TODO create recording  code
        isRecording = when(isRecording) {
            true -> false
            else -> true
        }

        if(isRecording) {

        }
    }
}