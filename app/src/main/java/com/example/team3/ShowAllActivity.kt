package com.example.team3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
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

        val userTheme = MyApplication.prefs.getString("theme", "default")
        Log.i("확인", userTheme)
        when (userTheme){
            "default"->setTheme(R.style.DefaultTheme)
            "light"->setTheme(R.style.LightTheme)
            "dark"->setTheme(R.style.DarkTheme)
            "pink"->setTheme(R.style.PinkTheme)
            "purple"->setTheme(R.style.PurpleTheme)
            "brown"->setTheme(R.style.BrownTheme)
            else->setTheme(R.style.DefaultTheme)
        }

        binding = ActivityShowAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDate()
        initData()
        initRecyclerView()
    }

    private fun initData() {
        for(i in dayList) {
            var date = "$year$month$i"
            var path = getExternalFilesDir(null).toString() + "/" + date
            var directory: File = File(path)
            if(!directory.exists()) continue
            Log.i("directory", directory.toString())
            var files = directory.listFiles()
            Arrays.sort(files)
            for (j in files) {
                Log.i("filename", i.toString())
                if(j.toString().contains("_data_")) continue
                if (j.toString().contains(".txt")) {
                    val inputStream = j.inputStream()
                    val dataText = inputStream.bufferedReader().use { it.readText() }
                    val sentence = dataText.split('\n')
                    val firstSentence = sentence[0]
                    Log.i("sentence", firstSentence)
                    memoData.add(MemoData(firstSentence, date))
                }
            }
            Log.i("memo", memoData.size.toString())
        }


    }

    private fun initRecyclerView() {
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
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