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
            else->setTheme(R.style.DefaultTheme)
        }

        binding = ActivityNotfSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "notification"
            val descriptionText = "알림"
            val channelId = "channel_id"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}