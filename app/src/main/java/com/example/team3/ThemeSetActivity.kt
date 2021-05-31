package com.example.team3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team3.databinding.ActivityThemeSetBinding

class ThemeSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityThemeSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userTheme = MyApplication.prefs.getString("theme", "default")
        when (userTheme){
            "default"->setTheme(R.style.DefaultTheme)
            "light"->setTheme(R.style.LightTheme)
            "dark"->setTheme(R.style.DarkTheme)
            else->setTheme(R.style.DefaultTheme)
        }

        binding = com.example.team3.databinding.ActivityThemeSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.apply {
            when (MyApplication.prefs.getString("theme","default")){
                "default"->{
                    defaultRgBtn.isChecked = true
                }
                "light"->{
                    lightRgBtn.isChecked = true
                }
                "dark"->{
                    darkRgBtn.isChecked = true
                }
            }
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.defaultRgBtn->{
                        MyApplication.prefs.setString("theme","default")
                    }
                    R.id.lightRgBtn ->{
                        MyApplication.prefs.setString("theme","light")
                    }
                    R.id.darkRgBtn ->{
                        MyApplication.prefs.setString("theme","dark")
                    }
                }
            }
            defaultBtn.setOnClickListener {
                defaultRgBtn.isChecked = true
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