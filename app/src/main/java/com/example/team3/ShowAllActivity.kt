package com.example.team3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team3.databinding.ActivityMainBinding
import com.example.team3.databinding.ActivityShowAllBinding
import java.io.File
import java.time.Year
import java.util.*
import java.util.Arrays.sort
import java.util.Collections.sort
import kotlin.collections.ArrayList

class ShowAllActivity : AppCompatActivity() {
    var memoData: ArrayList<MemoData> = ArrayList()
    var dayList = mutableListOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
    lateinit var binding: ActivityShowAllBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MemoAdapter
    lateinit var year: String
    lateinit var month: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDate()
        initData()
        initRecyclerView()
    }

    private fun initData() {
        for(i in dayList) {
            var date = "/$year$month$i"
            var path = getExternalFilesDir(null).toString() + date
            var directory: File = File(path)
            if(!directory.exists()) continue
            Log.i("directory", directory.toString())
            var files = directory.listFiles()
            Arrays.sort(files)
            for (i in files) {
                Log.i("filename", i.toString())
                if(i.toString().contains("_data_")) continue
                if (i.toString().contains(".txt")) {
                    val inputStream = i.inputStream()
                    val dataText = inputStream.bufferedReader().use { it.readText() }
                    val sentence = dataText.split('\n')
                    val firstSentence = sentence[0]
                    Log.i("sentence", firstSentence)
                    memoData.add(MemoData(firstSentence))
                }
            }
            Log.i("memo", memoData.size.toString())
        }


    }

    private fun initRecyclerView() {
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MemoAdapter(memoData)
        recyclerView.adapter = adapter
    }

    private fun initDate() {
        val i = intent
        var date = ""
        if (i.hasExtra("date")) {
            date = i.getStringExtra("date") ?: ""
        }
        var array = date.split("-")
        year = array[0]
        month = array[1]
        binding.apply {
            memoDate.text = (year + "년 " + month + "월")
        }
    }


}