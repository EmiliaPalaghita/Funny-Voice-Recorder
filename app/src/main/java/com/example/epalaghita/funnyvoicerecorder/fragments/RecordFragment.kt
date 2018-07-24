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
import kotlinx.android.synthetic.main.record_fragment.*
import java.io.File
import java.util.*


class RecordFragment : Fragment() {

    private val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"
    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val START_PLAY = "Start playing"
    private val STOP_PLAY = "Stop playing"
    private val START_RECORD = "Start recording"
    private val STOP_RECORD = "Stop recording"
    private var mFileName: String? = ""

    private var mRecorder: MediaRecorder? = null

    private var mPlayer: MediaPlayer? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.record_fragment, container, false)
    }

    private fun onRecord(start: Boolean) {
        if (start) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {

        record_button.setBackgroundResource(R.drawable.button_states_red)

        mFileName = PATH + Date().time.toString() + ".mp3"

        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile(mFileName)

        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        } catch (e2: Exception) {
            Log.e("EXCEPTIONS", e2.stackTrace.toString())
        }
        Log.d("FILENAME", mFileName)

        mRecorder?.start()

        chronometer1.start()
        chronometer1.base = SystemClock.elapsedRealtime()

    }

    private fun stopRecording() {
        record_button.setBackgroundResource(R.drawable.button_states)

        mRecorder?.stop()
        mRecorder?.reset()

        chronometer1.stop()
        chronometer1.base = SystemClock.elapsedRealtime()
    }

    private fun startPlaying() {
        play_button.text = STOP_PLAY
        mPlayer = MediaPlayer()
        try {
            mPlayer?.setDataSource(mFileName)
            mPlayer?.prepare()
            mPlayer?.start()
            mPlayer?.setOnCompletionListener {
                stopPlaying()
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

    }

    private fun stopPlaying() {
        play_button.text = START_PLAY
        mPlayer?.release()
        mPlayer = null
    }

    internal inner class RecordButton(ctx: Context) : Button(ctx) {
        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                record_button.text = STOP_RECORD
            } else {
                record_button.text = START_RECORD
            }
            mStartRecording = !mStartRecording
        }

        init {
            record_button.text = START_RECORD
            setOnClickListener(clicker)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.activity?.let {
            ActivityCompat.requestPermissions(it,
                    arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_RECORD_AUDIO_PERMISSION)
        }

        play_button.text = START_PLAY

        mRecorder = MediaRecorder()

        val audioDirectory = File(PATH)
        if (!audioDirectory.exists())
            audioDirectory.mkdirs()

        record_button.setOnClickListener(context?.let { RecordButton(it).clicker })

        play_button.setOnClickListener { startPlaying() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecorder?.release()
    }
}