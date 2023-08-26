package com.kotlin.soundrecorder

import android.os.SystemClock
import android.widget.Chronometer

class Timer() {

    fun startTimer(meter: Chronometer) {

        meter.base = SystemClock.elapsedRealtime();
        meter.start()
    }

    fun cancelTimer(meter: Chronometer) {

        meter.base = SystemClock.elapsedRealtime();
        meter.stop()
    }
}