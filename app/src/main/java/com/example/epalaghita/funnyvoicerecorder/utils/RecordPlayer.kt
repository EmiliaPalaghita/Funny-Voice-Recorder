package com.example.epalaghita.funnyvoicerecorder.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.IOException

interface RecordCallback {
    fun onMediaPlayerFinished()
}

object RecordPlayer {

    val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"

    var mPlayer: MediaPlayer? = null
    var startRecording: Boolean = true
    var mRecorder: MediaRecorder? = null

    fun playRecord(path: String, completion: RecordCallback) {
        mPlayer = MediaPlayer()
        mPlayer?.setDataSource(path)
        mPlayer?.prepare()
        mPlayer?.start()
        mPlayer?.setOnCompletionListener {
            completion.onMediaPlayerFinished()
            mPlayer?.release()
            mPlayer = null
        }
    }

    fun startRecording(path: String) {
        if (mRecorder == null)
            mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile(path)

        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
            Log.e("AudioRecordTest", "prepare() failed")
        } catch (e2: Exception) {
            Log.e("EXCEPTIONS", e2.stackTrace.toString())
        }

        mRecorder?.start()
    }

    fun destroyRecorder() {
        mRecorder?.release()
    }

    fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.reset()
    }

    fun switchRecordingBool() {
        startRecording = when (startRecording) {
            true -> false
            else -> true
        }
    }
}