package com.example.team3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.team3.databinding.ActivityMainBinding



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
                startActivity(calendarIntent)
            }
        }
    }
}