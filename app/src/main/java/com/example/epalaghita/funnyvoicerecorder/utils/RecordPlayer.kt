package com.example.epalaghita.funnyvoicerecorder.utils

import android.media.*
import android.os.Environment
import android.util.Log
import java.io.*


interface RecordCallback {
    fun onMediaPlayerFinished()
}

object RecordPlayer {

    val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"

    private var mPlayer: MediaPlayer? = null
    var startRecording: Boolean = true
    private var mRecorder: MediaRecorder? = null
    private var filePath: String = ""
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
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

    fun startRecordingWithAudioRecord(path: String) {
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer)
        recorder?.startRecording()
        isRecording = true
        recordingThread = Thread(Runnable { writeAudioDataToFile(path) }, "AudioRecorder Thread")
        recordingThread?.start()
    }

    fun playRecordWithAudioTrack(path: String, completion: RecordCallback) {
        val file = File(path)
        val minBuffer = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val byteArray = ByteArray(minBuffer)
        val inputStream = FileInputStream(path)

        val audioTrack = AudioTrack(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
                AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .build(),
                minBuffer, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE)

        audioTrack.play()
        var i = inputStream.read(byteArray)

        while (i != -1) {
            audioTrack.write(byteArray, 0, i)
            i = inputStream.read(byteArray)
        }

        completion.onMediaPlayerFinished()

    }

    private fun writeAudioDataToFile(path: String) {
        val byteArray = ByteArray(buffer)
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(path)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        while (isRecording) {
            recorder?.read(byteArray, 0, buffer)
            os?.write(byteArray, 0, buffer)

        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

    fun stopRecordingWithAudioRecord() {
        isRecording = false
        recorder?.stop()
        recorder?.release()
        recorder = null
        recordingThread = null
    }


    fun startRecording(path: String) {
        this.filePath = path

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