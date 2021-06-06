package com.example.team3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team3.databinding.ActivityNotfSetBinding

class NotfSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotfSetBinding

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

        binding = ActivityNotfSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.apply {
            rbPriority.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId){
                    R.id.rb_min->{
                        //NotificationManager에서는 1, NotificationCompat에서는 -2
                        MyApplication.prefs.setString("priority", "1")
                    }
                    R.id.rb_low->{
                        //NotificationManager에서는 2, NotificationCompat에서는 -1
                        MyApplication.prefs.setString("priority", "2")
                    }
                    R.id.rb_default->{
                        //NotificationManager에서는 3, NotificationCompat에서는 0
                        MyApplication.prefs.setString("priority", "3")
                    }
                    R.id.rb_high->{
                        //NotificationManager에서는 4, NotificationCompat에서는 1
                        MyApplication.prefs.setString("priority", "4")
                    }
                }
            }
        }
    }
}