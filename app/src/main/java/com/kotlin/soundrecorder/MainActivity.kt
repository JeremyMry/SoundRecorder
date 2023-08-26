package com.kotlin.soundrecorder

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Chronometer
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private val resetButton:Button by lazy {
        findViewById(R.id.button_stop_recording)
    }
    private val recordButton:Button by lazy {
        findViewById(R.id.button_recording)
    }
    private val meter:Chronometer by lazy {
        findViewById(R.id.recording_timer)
    }

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.wav"
    }

    private val requiredPermissions = arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE)

    private val timer = Timer()

    private val record = Recorder()

    private val folderManager = FolderManager()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Permission method
        activityResultLauncher.launch(requiredPermissions)

        // Create recording folder and get the path
        val folder = filesDir
        val mainFolderPath = folderManager.createInitialFolder(folder)

        recordButton.setOnClickListener {

            // Create folder for this record
            val specificRecordPath = folderManager.createSpecificRecordFolder(mainFolderPath)

            record.startRecording(specificRecordPath)
            timer.startTimer(meter)
        }

        resetButton.setOnClickListener {
            record.stopRecording()
            timer.cancelTimer(meter)
        }
    }

    // Handle permissions (write/record) + shut down the app if the permissions are manually locked
    @RequiresApi(Build.VERSION_CODES.M)
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions -> permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (!isGranted) {

                    if(permissionName == RECORD_AUDIO) {

                        println(permissionName)

                        AlertDialog.Builder(this)
                            .setTitle("Permission Needed")
                            .setMessage("Please accept the recording permission, the application cannot work without")
                            .setPositiveButton("OK" ) { dialog, which ->
                                finish()
                            }.create().show();

                    } else if(permissionName == WRITE_EXTERNAL_STORAGE) {

                        println(permissionName)

                        AlertDialog.Builder(this)
                            .setTitle("Permission Needed")
                            .setMessage("Please accept the storage permission manually, the application cannot work without")
                            .setPositiveButton("OK" ) { dialog, which ->
                                finish()
                            }.create().show();
                    }
                }
            }
        }

    // create a recording folder for the app the first time it's launched
    /*private fun createInitialFolder(): File {
        val folder = filesDir
        val f = File(folder, "records")

        if (!f.exists()) {
            f.mkdir()
        }

        return f
    }*/


}