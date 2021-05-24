package com.example.team3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.team3.databinding.ActivityNotfSetBinding

class NotfSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotfSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotfSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}