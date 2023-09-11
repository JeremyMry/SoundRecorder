package com.kotlin.soundrecorder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RecordsList: AppCompatActivity() {

    private val recordsList: ListView by lazy {
        findViewById(R.id.recordlist)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.records_list)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val records = setListContent()

        recordsList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            if(records[id.toInt()] != "") {

                val intent = Intent(this, RecordList::class.java)
                intent.putExtra("listFiles", records[id.toInt()])
                startActivity(intent)
            }
        }
    }

    private fun setListContent(): Array<String> {

        val arrayAdapter: ArrayAdapter<*>
        var records = arrayOf("")

        val folder = filesDir
        val f = File(folder, "records")
        val names = f.list()

        for (name in names) {
            records += name
        }

        records += ""

        var mListView = findViewById<ListView>(R.id.recordlist)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records)
        mListView.adapter = arrayAdapter

        return records
    }
}