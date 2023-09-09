package com.kotlin.soundrecorder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RecordList: AppCompatActivity() {

    private val recordsListU: ListView by lazy {
        findViewById(R.id.recordlistU)
    }
    private val deleteButton: Button by lazy {
        findViewById(R.id.delete)
    }
    private val sendToAiButton: Button by lazy {
        findViewById(R.id.sendToAI)
    }

    private var mediaPlayer: MediaPlayer? = null

    private var reader: Boolean = false

    private val sendAudioFile = SendAudioFile()

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_list)

        val list = intent.getStringExtra("listFiles")
        val records = setListContent(list)

        val folder = filesDir
        val f = File(folder, "records")
        val f1 = File(f, list)
        val f2 = File(f1, "/record.wav")

        mediaPlayer = MediaPlayer.create(this, Uri.parse(f2.toString()))

        recordsListU.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            if(records[id.toInt()] != "") {

                if(!reader) {

                    reader = true

                    val folder = filesDir
                    val f = File(folder, "records")
                    val f1 = File(f, list)
                    val f2 = File(f1, "/record.wav")

                    mediaPlayer = MediaPlayer.create(this, Uri.parse(f2.toString()))
                    mediaPlayer?.isLooping = false
                    mediaPlayer?.start()

                } else {

                    reader = false
                    mediaPlayer?.stop()

                }
            }
        }

        sendToAiButton.setOnClickListener {

            val folder = filesDir
            val f = File(folder, "records")
            val f1 = File(f, list)
            val f2 = File(f1, "/record.wav")

            val summarize = sendAudioFile.sendAudioFile(f2)

            if( summarize == "error") {

                AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("An error occurred, during the process").create().show();

            } else {

                AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("The summarize has been generated").create().show();

                println(summarize)
            }
        }

        deleteButton.setOnClickListener {

            val folder = filesDir
            val f = File(folder, "records")
            val f1 = File(f, list)
            f1.deleteRecursively()

            val intent = Intent(this, RecordsList::class.java)
            startActivity(intent)
        }
    }

    private fun setListContent(list: String?): Array<String> {

        val folder = filesDir
        val f = File(folder, "records")
        val f1 = File(f, list)
        val names = f1.list()

        val arrayAdapter: ArrayAdapter<*>
        var records = arrayOf("")

        for (name in names) {
            records += name
        }

        var mListView = findViewById<ListView>(R.id.recordlistU)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records)
        mListView.adapter = arrayAdapter

        return records
    }
}