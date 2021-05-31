package com.example.team3

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        val intent = Intent(this, SettingActivity::class.java)
        binding.apply {
            imageMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            settingBtn.setOnClickListener {
                startActivity(intent)
            }
        }
    }
}