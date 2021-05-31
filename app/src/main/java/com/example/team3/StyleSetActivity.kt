package com.example.team3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.team3.databinding.ActivityStyleSetBinding

class StyleSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityStyleSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStyleSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.lightRgBtn ->{

                    }
                    R.id.darkRgBtn ->{

                    }
                }
            }

            lightBtn.setOnClickListener {
                lightRgBtn.isChecked = true
            }
            darkBtn.setOnClickListener {
                darkRgBtn.isChecked = true
            }

        }
    }
}