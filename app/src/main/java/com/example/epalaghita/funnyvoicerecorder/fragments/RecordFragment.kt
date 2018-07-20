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
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.record_fragment.*
import java.util.*


class RecordFragment : Fragment() {

    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var mFileName: String? = Date().time.toString()

    private var mRecordButton: RecordButton? = null
    private var mRecorder: MediaRecorder? = null

    private val mPlayButton: PlayButton? = null
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

        mFileName = Environment.getExternalStorageDirectory().absolutePath + Date().time.toString() + ".mp3"

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setOutputFile(mFileName)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        //TODO - try to solve the EACCES permission denied error
        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

        mRecorder?.prepare()
        mRecorder?.start()
    }

    private fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
    }

    private fun onPlay(start: Boolean) {
        if (start) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer?.setDataSource(mFileName)
            mPlayer?.prepare()
            mPlayer?.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun stopPlaying() {
        mPlayer?.release()
        mPlayer = null
    }

    internal inner class RecordButton(ctx: Context) : Button(ctx) {
        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                text = "Stop recording"
            } else {
                text = "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : Button(ctx) {
        private var mStartPlaying = true

        private var clicker: View.OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = if (mStartPlaying) {
                "Stop playing"
            } else {
                "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_RECORD_AUDIO_PERMISSION) }

        //  TODO - create directory for all recordings
        val dir: String = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory"

        record_button.setOnClickListener(context?.let { RecordButton(it).clicker })
    }
}