package com.example.epalaghita.funnyvoicerecorder.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import omrecorder.AudioRecordConfig
import omrecorder.OmRecorder
import omrecorder.PullTransport
import omrecorder.PullableSource
import omrecorder.Recorder
import java.io.File


interface RecordCallback {
    fun onMediaPlayerFinished()
}

object RecordPlayer {

    val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"

    private var mPlayer: MediaPlayer? = null
    var startRecording: Boolean = true
    private var mRecorder: MediaRecorder? = null
    private lateinit var omRecorder: Recorder
    val buffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT)


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

    private fun mic(): PullableSource {
        return PullableSource.Default(
                AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        )
    }

    private fun file(path: String): File {
        return File(path)
    }

    fun startRecording(path: String) {
        omRecorder = OmRecorder.wav(PullTransport.Default(mic()), file(path))
        omRecorder.startRecording()
    }

    fun destroyRecorder() {
        mRecorder?.release()
    }

    fun stopRecording() {
        omRecorder.stopRecording()
    }

    fun switchRecordingBool() {
        startRecording = when (startRecording) {
            true -> false
            else -> true
        }
    }


}