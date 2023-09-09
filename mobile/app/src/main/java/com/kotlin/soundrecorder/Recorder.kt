package com.kotlin.soundrecorder

import android.media.MediaRecorder

class Recorder() {

    private var recorder: MediaRecorder? = null

    fun startRecording(recordingFilePath : String) {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
    }

    fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
    }
}