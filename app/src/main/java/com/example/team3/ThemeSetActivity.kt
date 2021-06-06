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
            "pink"->setTheme(R.style.PinkTheme)
            "purple"->setTheme(R.style.PurpleTheme)
            "brown"->setTheme(R.style.BrownTheme)
            else->setTheme(R.style.DefaultTheme)
        }

        binding = ActivityThemeSetBinding.inflate(layoutInflater)
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
                "pink"->{
                    pinkRgBtn.isChecked = true
                }
                "purple"->{
                    purpleRgBtn.isChecked = true
                }
                "brown"->{
                    brownRgBtn.isChecked = true
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
                    R.id.pinkRgBtn->{
                        MyApplication.prefs.setString("theme","pink")
                    }
                    R.id.purpleRgBtn->{
                        MyApplication.prefs.setString("theme","purple")
                    }
                    R.id.brownRgBtn->{
                        MyApplication.prefs.setString("theme","brown")
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
            pinkBtn.setOnClickListener {
                pinkRgBtn.isChecked = true
            }
            purpleBtn.setOnClickListener {
                purpleRgBtn.isChecked = true
            }
            brownBtn.setOnClickListener {
                brownRgBtn.isChecked = true
            }

        }
    }

}