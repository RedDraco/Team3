package com.example.team3

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.calenderapp_1.DummyActivity
import com.example.team3.databinding.*
import java.util.*

class MainActivityC : AppCompatActivity() {
    lateinit var binding: ActivityMaincBinding

    //
    var DUMMY_REQUEST = 0

    //*알람 관련 변수들
    var mymemo = ""
    var myampm = ""
    var myhour = 0
    var mymin = 0
    var message = ""
    var alarmflag = 0
    //*

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaincBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){

        //****날짜 버튼**** -> 뒤로가기 기능.
        binding.TextDate.setOnClickListener {
            binding.TempText.setText("날짜 눌림")
        }
        //****날짜 버튼****

        //*****왼쪽, 오른쪽 버튼******
        //전날, 다음날로 각각 넘겨진다.
        binding.apply {
            LeftBtn.setOnClickListener {
                binding.TempText.setText("왼쪽 버튼 눌림")
            }
            RightBtn.setOnClickListener {
                binding.TempText.setText("오른쪽 버튼 눌림")
            }
        }
        //*****왼쪽, 오른쪽 버튼******


        //*****추가버튼*****
        binding.Plusbtn.setOnClickListener {
            //여기다가 하루 추가 액티비티 연결.
            val intent = Intent(this, DummyActivity::class.java)
            startActivityForResult(intent, DUMMY_REQUEST)
        }
        //*****추가버튼*****

        //*****알람*****
        if(alarmflag == 0){
            binding.TextAlarm.setText("추가하기")
        }
        binding.TextAlarm.setOnClickListener {
            val dlgBinding = MypickerdlgBinding.inflate(layoutInflater)
            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setView(dlgBinding.root)
                    //알람 추가 - **미완
                    .setPositiveButton("추가"){
                        _,_->
                        alarmflag = 1
                        mymemo = dlgBinding.EditAlarm.text.toString()
                        myhour = dlgBinding.timePicker.hour
                        //오전 오후 설정
                        if(myhour>=12 && myhour <= 24){
                            myampm = "오후"
                        }
                        else{
                            myampm = "오전"
                        }
                        mymin = dlgBinding.timePicker.minute
                        mymemo = dlgBinding.EditAlarm.text.toString()
                        //알람 옆에 텍스트 설정
                        message = myampm + " " + myhour.toString() + " : " + mymin.toString()
                        binding.TextAlarm.setText(message)
                        //스위치 온
                        binding.AlarmSwitch.isChecked = true

                        //***푸쉬알람
                        val timerTask = object : TimerTask() {
                            override fun run() {
                                makeNotification()
                            }
                        }
                        val timer = Timer()
                        timer.schedule(timerTask, 2000)
                        //*** -> 정해진 시간에 오는 알람으로 대체해야함.

                        //toast 메시지 출력
                        Toast.makeText(this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    //알람 취소 버튼
                    .setNegativeButton("취소"){
                        _,_->
                        //아무것도 안한다.
                    }
                    //알람 삭제
                    .setNeutralButton("삭제"){
                        _,_->
                        binding.TextAlarm.setText("추가하기")
                        //알람이 삭제되면 스위치도 off
                        binding.AlarmSwitch.isChecked = false

                        Toast.makeText(this, "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }
        //*****알람*****
    }

    //액티비티를 돌려받음과 함께 Request정보도 같이 받음
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            //**임시 - 더미 액티비티에서 받은 Request 값
            DUMMY_REQUEST->{
                if(resultCode == 1){
                    binding.TempText.setText("Request 1번 무사히 받음")
                }
                else if(resultCode == 2){
                    binding.TempText.setText("Request 2번 무사히 받음")
                }
            }
            //**임시 - 무사히 받는 것을 확인
        }
    }

    //**미완 - 푸시알림을 띄워주는
    //정해진 시간에 보내기??
    fun makeNotification(){
        val id = "MyChannel"
        val name = "TimeCheckChannel"
        val notificationChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val builder = NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentTitle("일정 알람")
                .setContentText(mymemo)
                .setAutoCancel(true)

        val intent = Intent(this, MainActivityC::class.java)
        intent.putExtra("time", mymemo)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        val notification = builder.build()
        manager.notify(10, notification)
    }


}