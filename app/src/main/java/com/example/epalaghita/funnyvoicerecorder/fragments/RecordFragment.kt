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
import android.os.AsyncTask
import android.os.Environment
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.epalaghita.funnyvoicerecorder.soundtouch.SoundTouch
import com.example.epalaghita.funnyvoicerecorder.utils.RecordCallback
import com.example.epalaghita.funnyvoicerecorder.utils.RecordPlayer
import com.example.epalaghita.funnyvoicerecorder.utils.RecordPlayer.PATH
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

        mFileName = RecordPlayer.PATH + Date().time.toString() + ".wav"
        val path: String = mFileName as String
        Log.d("FILENAME", mFileName)

        RecordPlayer.startRecording(path)

//        RecordPlayer.startRecordingWithAudioRecord(path)

        chronometer1.start()
        chronometer1.base = SystemClock.elapsedRealtime()
    }

    private fun stopRecording() {
        val pitch = Integer.parseInt(pitch_editText.text.toString())
        val speed = Integer.parseInt(speed_editText.text.toString())

        record_button.setBackgroundResource(R.drawable.button_states)
        record_button.text = START_RECORD
        RecordPlayer.stopRecording()

        chronometer1.stop()
        chronometer1.base = SystemClock.elapsedRealtime()

//        RecordPlayer.stopRecordingWithAudioRecord()

//        processRecording(pitch, speed)
    }

    private fun processRecording(pitch: Int, speed: Int) {
        val processTask = ProcessTask()
        val params: ProcessTask.Parameters = processTask.Parameters()
        params.input = mFileName
        params.output = PATH + "temp.wav"
        params.speed = speed
        params.pitch = pitch

        Toast.makeText(this.context, "Starting to process file  $params.input ...", Toast.LENGTH_SHORT).show()

        processTask.execute(params)
    }

    class ProcessTask : AsyncTask<ProcessTask.Parameters, Int, Long>() {


        inner class Parameters {
            var input: String? = ""
            var output: String = ""
            var speed = 100
            var pitch = 0
        }

        private fun doSoundTouchProcessing(params: Parameters): Long {
            val st = SoundTouch()
            st.setPitchSemiTones(params.pitch.toFloat())
            st.setSpeed(params.speed.toFloat())
            val res = st.processFile(params.input, params.output)

            if (res != 0) {
                Log.e("FAILURE AT PROCESSING", SoundTouch.getErrorString())
                return -1L
            }

            return 0L
        }

        override fun doInBackground(vararg p0: Parameters): Long {
            return doSoundTouchProcessing(p0[0])
        }
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
                    arrayOf(Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
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