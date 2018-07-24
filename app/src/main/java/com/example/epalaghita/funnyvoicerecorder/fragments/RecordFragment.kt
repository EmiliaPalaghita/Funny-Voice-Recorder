package com.example.epalaghita.funnyvoicerecorder.fragments

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.epalaghita.funnyvoicerecorder.R
import java.io.IOException
import android.media.MediaPlayer
import android.os.Environment
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.epalaghita.funnyvoicerecorder.utils.RecordCallback
import com.example.epalaghita.funnyvoicerecorder.utils.RecordPlayer
import kotlinx.android.synthetic.main.record_fragment.*
import java.io.File
import java.util.*


class RecordFragment : Fragment() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val START_PLAY = "Start playing"
    private val STOP_PLAY = "Stop playing"
    private val START_RECORD = "Start recording"
    private val STOP_RECORD = "Stop recording"
    private var mFileName: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.record_fragment, container, false)
    }

    private fun onRecord() {

        if (RecordPlayer.startRecording) {
            startRecording()

        } else {
            stopRecording()
        }

        RecordPlayer.switchRecordingBool()

    }

    private fun startRecording() {
        record_button.setBackgroundResource(R.drawable.button_states_red)
        record_button.text = STOP_RECORD

        mFileName = RecordPlayer.PATH + Date().time.toString() + ".mp3"
        val path: String = mFileName as String

        RecordPlayer.startRecording(path)

        chronometer1.start()
        chronometer1.base = SystemClock.elapsedRealtime()
    }

    private fun stopRecording() {
        record_button.setBackgroundResource(R.drawable.button_states)
        record_button.text = START_RECORD
        RecordPlayer.stopRecording()

        chronometer1.stop()
        chronometer1.base = SystemClock.elapsedRealtime()
    }

    private fun playRecord() {
        play_button.text = STOP_PLAY
        try {
            val path: String = this.mFileName!!
            RecordPlayer.playRecord(path, object : RecordCallback {
                override fun onMediaPlayerFinished() {
                    play_button.text = START_PLAY
                }

            })
        } catch (e: Exception) {
            Toast.makeText(this.context, "Try recording first!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.activity?.let {
            ActivityCompat.requestPermissions(it,
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_RECORD_AUDIO_PERMISSION)
        }

        createUI()

        createAudioDirectory()

        record_button.setOnClickListener { onRecord() }

        play_button.setOnClickListener { playRecord() }
    }

    override fun onDestroy() {
        super.onDestroy()
        RecordPlayer.destroyRecorder()
    }

    private fun createUI() {
        play_button.text = START_PLAY
        record_button.text = START_RECORD
    }

    private fun createAudioDirectory() {
        val audioDirectory = File(RecordPlayer.PATH)
        if (!audioDirectory.exists())
            audioDirectory.mkdirs()
    }
}