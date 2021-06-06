package com.example.team3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team3.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    //val compArr = arrayOf("TextView", "Button", "RadioButton", "Switch", "EditText")

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

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            changeTheme.setOnClickListener{
                val intent = Intent(this@SettingActivity, ThemeSetActivity::class.java)
                startActivity(intent)
            }
            setLock.setOnClickListener {
                val intent = Intent(this@SettingActivity, LockSetActivity::class.java)
                startActivity(intent)
            }
            setNotification.setOnClickListener{
                val intent = Intent(this@SettingActivity, NotfSetActivity::class.java)
                startActivity(intent)
            }

        }
    }
}