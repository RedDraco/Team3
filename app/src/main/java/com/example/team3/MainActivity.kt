package com.example.team3

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.team3.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.navigationView.itemIconTintList = null
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        val calendarIntent = Intent(this, MainActivityC::class.java)
        binding.apply {
            imageMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            settingBtn.setOnClickListener {
                startActivity(settingIntent)
            }
            calendarView3.setOnDateChangeListener { view, year, month, dayOfMonth ->
                calendarIntent.putExtra("year", year.toString())

                val MONTH = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                calendarIntent.putExtra("month", MONTH)

                val DAY = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                calendarIntent.putExtra("day", DAY)

                startActivity(calendarIntent)
            }
        }
    }
}