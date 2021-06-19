package com.example.team3

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.team3.databinding.ActivityNotfSetBinding
import java.util.*

class NotfSetActivity : AppCompatActivity() {
    var temp_priority = MyApplication.prefs.getString("priority", "default")
    var alarmDBHelper = AlarmDBHelper(this, "alarmDB.db")
    lateinit var binding: ActivityNotfSetBinding
    lateinit var alarmMgr: AlarmManager

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
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        binding.apply {
            when (MyApplication.prefs.getString("priority", "default")){
                "high"-> rbHigh.isChecked = true
                "default"-> rbDefault.isChecked = true
                "low"-> rbLow.isChecked = true
                "min"-> rbMin.isChecked = true
            }

            rbPriority.setOnCheckedChangeListener { group, checkedId ->
                temp_priority = MyApplication.prefs.getString("priority", "default")
                when (checkedId){
                    R.id.rb_min->{
                        MyApplication.prefs.setString("priority", "min")
                        changePriority()
                    }
                    R.id.rb_low->{
                        MyApplication.prefs.setString("priority", "low")
                        changePriority()
                    }
                    R.id.rb_default->{
                        MyApplication.prefs.setString("priority", "default")
                        changePriority()
                    }
                    R.id.rb_high->{
                        MyApplication.prefs.setString("priority", "high")
                        changePriority()
                    }
                }
            }
        }
    }

    fun changePriority() {//priority 설정 값 바뀔 때 이전에 등록되었던 alarm 취소하고 재등록
        lateinit var intent: Intent
        when (temp_priority){
            "high"-> intent = Intent(applicationContext, ReceiverHigh::class.java)
            "default"-> intent = Intent(applicationContext, ReceiverDefault::class.java)
            "low" -> intent = Intent(applicationContext, ReceiverLow::class.java)
            "min" -> intent = Intent(applicationContext, ReceiverMin::class.java)
        }
        if (alarmDBHelper.getRowCount()>0){
            //cancel all alarms
            val allAlarms = alarmDBHelper.getAllRecord()
            for (i in allAlarms.indices){
                val alarmIntent = PendingIntent.getBroadcast(applicationContext, allAlarms[i].id, intent, PendingIntent.FLAG_NO_CREATE)
                if (alarmIntent != null){
                    alarmMgr?.cancel(alarmIntent)
                    alarmDBHelper.deleteAlarm(allAlarms[i].id)
                }
            }
            //set alarms again
            for (i in allAlarms.indices){
                setAlarm(allAlarms[i].year, allAlarms[i].month, allAlarms[i].day, allAlarms[i].hour, allAlarms[i].minute)
            }
        }
    }

    fun setAlarm(year: Int, month:Int, day: Int, hour: Int, minute: Int){
        lateinit var alarmIntent : PendingIntent
        var cnt = MyApplication.prefs.getInt("cnt",0)

        when (MyApplication.prefs.getString("priority", "default")){
            "high"->{
                alarmIntent = Intent(applicationContext, ReceiverHigh::class.java).let{
                        intent->
                    PendingIntent.getBroadcast(applicationContext, cnt, intent, 0)
                }
            }
            "default"->{
                alarmIntent = Intent(applicationContext, ReceiverDefault::class.java).let{
                        intent->
                    PendingIntent.getBroadcast(applicationContext, cnt, intent, 0)
                }
            }
            "low"->{
                alarmIntent = Intent(applicationContext, ReceiverLow::class.java).let{
                        intent->
                    PendingIntent.getBroadcast(applicationContext, cnt, intent, 0)
                }
            }
            "min"->{
                alarmIntent = Intent(applicationContext, ReceiverMin::class.java).let{
                        intent->
                    PendingIntent.getBroadcast(applicationContext, cnt, intent, 0)
                }
            }
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        alarmMgr?.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        val alarmData = MyAlarmData(cnt,"${hour}시 ${minute}분", year, month, day, hour, minute)
        alarmDBHelper.insertAlarm(alarmData)
        val allAlarms = alarmDBHelper.getAllRecord()
        MyApplication.prefs.setInt("cnt", cnt+1)
    }
}