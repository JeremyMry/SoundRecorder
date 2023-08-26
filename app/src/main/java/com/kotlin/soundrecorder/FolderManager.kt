package com.kotlin.soundrecorder

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FolderManager {

    // create a recording folder for the app the first time it's launched
    fun createInitialFolder(file : File): File {

        val f = File(file, "records")

        if (!f.exists()) {
            f.mkdir()
        }

        return f
    }

    // create a specific folder with unique name for the current record
    @RequiresApi(Build.VERSION_CODES.O)
    fun createSpecificRecordFolder(file: File): String {

        val dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")
        val localDateFormatted = LocalDateTime.now().format(dateFormatter)
        val f1 = File(file, localDateFormatted);
        f1.mkdirs();

        return "$f1/record.wav"
    }
}