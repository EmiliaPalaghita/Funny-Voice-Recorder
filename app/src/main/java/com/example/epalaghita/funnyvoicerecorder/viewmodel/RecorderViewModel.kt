package com.example.epalaghita.funnyvoicerecorder.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.example.epalaghita.funnyvoicerecorder.soundtouch.SoundTouch
import com.example.epalaghita.funnyvoicerecorder.utils.RecordCallback
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import omrecorder.AudioRecordConfig
import omrecorder.OmRecorder
import omrecorder.PullTransport
import omrecorder.PullableSource
import omrecorder.Recorder
import omrecorder.WriteAction
import java.io.File

interface RecordCallback {
    fun onMediaPlayerFinished()
}

class SoundTouchException(override var message: String) : Exception(message)

class RecorderViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "RecorderViewModel"
        val PATH = Environment.getExternalStorageDirectory().absolutePath + "/_audioDirectory/"
        private const val RECORDING_FREQUENCY = 44100
        private const val SILENCE_THRESHOLD = 200L
        private const val IN_FILENAME = "voice.wav"
        private const val OUT_FILENAME = "voiceModified.wav"
        private const val DEFAULT_SPEED = 1.1f
        private const val DEFAULT_PITCH = 7f
        private const val DEFAULT_TEMPO = 1.5f
        private const val STEP_SPEED = 0.1f
        private const val STEP_PITCH = 0.5f
        private const val STEP_TEMPO = 0.1f
        private const val STEP_THRESHOLD = 50L
    }

    private val mediaPlayer = MediaPlayer()
    private val disposable = CompositeDisposable()

    private val microphoneConfig = AudioRecordConfig.Default(
            MediaRecorder.AudioSource.MIC,
            AudioFormat.ENCODING_PCM_16BIT,
            AudioFormat.CHANNEL_IN_MONO,
            RECORDING_FREQUENCY
    )

    private val writeAction = WriteAction.Default()
    private val writeFile = File(application.applicationContext.filesDir, IN_FILENAME)
    private val readFile = File(application.applicationContext.filesDir, OUT_FILENAME)

    private lateinit var recorder: Recorder

    private fun handleError() {
        Log.d(TAG, "Error at playing")
    }

    fun startRecording() {
        recorder = OmRecorder.wav(
                PullTransport.Default(
                        PullableSource.Default(microphoneConfig)
                ),
                writeFile
        )
        recorder.startRecording()
    }

    private fun playFile(completion: RecordCallback) {
        mediaPlayer.setDataSource(readFile.path)
        mediaPlayer.prepare()
        mediaPlayer.setOnCompletionListener {
            completion.onMediaPlayerFinished()
            mediaPlayer.reset()
        }
        mediaPlayer.start()
    }

    fun stopRecording() {
        recorder.stopRecording()
    }

    private fun processFile(): Completable {
        return Completable.fromCallable {
            val st = SoundTouch()
            st.setTempo(DEFAULT_TEMPO)
            st.setPitchSemiTones(DEFAULT_PITCH)
            st.setSpeed(DEFAULT_SPEED)
            val startTime = System.currentTimeMillis()
            val res = st.processFile(writeFile.path, readFile.path)
            val endTime = System.currentTimeMillis()
            val duration = (endTime - startTime) * 0.001f

            Log.i("SoundTouch", "process file done, duration = $duration")
            if (res != 0) {
                throw SoundTouchException(SoundTouch.getErrorString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun playRecord(completion: RecordCallback) {
        disposable.add(
                processFile()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    playFile(completion)
                                },
                                {
                                    handleError()
                                }
                        )
        )
    }


}