package com.example.team3

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team3.databinding.*
import java.io.File
import java.util.*

class MainActivityC() : AppCompatActivity() {
    var iconList = arrayListOf<IconData>()
    lateinit var binding: ActivityMaincBinding
    lateinit var iconBinding:IcondlgBinding
    lateinit var iconAdapter: IconAdapter

    //
    var ADD_REQUEST = 0
    var todayDate = ""
    //전 액티비티에서 받아오는 날짜 정보
    var YEAR = ""
    var MONTH = ""
    var DAY = ""
    //
    var SavePATH = ""
    //ADDMEMO에서 받아오는 파일 디렉토리
    var PATH = ""

    //**알람 관련 변수들
    var mymemo = ""
    var myampm = ""
    var myhour = 0
    var mymin = 0
    var message = ""
    var alarmflag = 0
    //**

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaincBinding.inflate(layoutInflater)
        iconBinding = IcondlgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        if(i.hasExtra("year") && i.hasExtra("month") && i.hasExtra("day")) {
            YEAR = i.getStringExtra("year")?:""
            MONTH = i.getStringExtra("month")?:""
            DAY = i.getStringExtra("day")?:""
            todayDate = "/" + YEAR + MONTH + DAY

            Log.i("MainActivityC", "$todayDate")
        }

        initData()
        init()
        initIconRecycler()
    }

    private fun initData(){
        iconList.add(IconData("icon_1"))
        iconList.add(IconData("icon_2"))
        iconList.add(IconData("icon_3"))
        iconList.add(IconData("icon_4"))
        iconList.add(IconData("icon_5"))
        iconList.add(IconData("icon_6"))
    }

    private fun initIconRecycler(){
        iconBinding.iconRecycler.layoutManager =  GridLayoutManager(this, 3)
        iconAdapter = IconAdapter(this, iconList)

        iconAdapter.itemClickListener = object :IconAdapter.OnItemClickListener{

            override fun OnItemClick(holder: IconAdapter.ViewHolder, view: View, iconData: IconData, position: Int) {
                val resourceId = resources.getIdentifier(iconList[position].photo, "drawable", packageName)
                binding.ImageIcon.setImageResource(resourceId)
            }

        }
        iconBinding.iconRecycler.adapter = iconAdapter
    }

    private fun init(){

        //클릭한 날짜에 따라, 날짜 텍스트 설정
        binding.TextDate.setText(MONTH + "월 " + DAY + "일")


        //****아이콘 버튼****
        binding.ImageIcon.setOnClickListener {

            val icondlgBuilder = AlertDialog.Builder(this)

            icondlgBuilder.setView(iconBinding.root)
                .setPositiveButton("확인"){
                        _,_->
                    //아무것도 안한다.
                }
                .show()
        }
        //****아이콘 버튼****

        //****저장 버튼****
        binding.Savebtn.setOnClickListener {
            saveDBDay()
        }
        //****저장 버튼****

        //****날짜 버튼**** -> 뒤로가기 기능.
        binding.TextDate.setOnClickListener {
            saveDBDay()
            this.finish()
        }
        //****날짜 버튼****

        //*****왼쪽, 오른쪽 버튼******
        //전날, 다음날로 각각 넘겨진다.
        binding.apply {
            LeftBtn.setOnClickListener {

            }
            RightBtn.setOnClickListener {

            }
        }
        //*****왼쪽, 오른쪽 버튼******


        //*****추가버튼*****
        binding.Plusbtn.setOnClickListener {
            //여기다가 하루 추가 액티비티 연결.
            val intent = Intent(this, AddMemo::class.java)
            //intent.putExtra("date", "/Today_date")
            startActivityForResult(intent, ADD_REQUEST)
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
            ADD_REQUEST->{
                if(resultCode == Activity.RESULT_OK){
                    if(data?.hasExtra("path")!!){
                        PATH = data?.getStringExtra("path")!!
                        Log.i("MainActivityC", "$PATH")
                        val file = File(PATH)
                        decideExtra(PATH)
                        //사용한 file은 더 이상 이용하지 않는다. (?)
                        //계속 저장하다보면 메모리 낭비!!
                        file.delete()
                    }
                }
            }
        }
    }
    //

    //****AddMemo 액비티티에서 전달받은 memoPath의 확장자 확인
    private fun decideExtra(PATH : String) {
        if(PATH == "")
            return
        else{
            val splitString = PATH.split('.')
            val extension = splitString.last()

            //*확장자에 따라 FrameLayout에 동적으로 메모 추가.
            when(extension){
                "txt" -> makeTextMemo(PATH)
                "jpg" -> makePictureMemo(PATH)
                "png" -> makeDrawingMemo(PATH)
                else ->  return
            }
        }
    }
    //****

    //****동적 메모 할당
    private fun makeDrawingMemo(PATH: String) {
        setDynamicLL(binding.llDynamic, PATH, 3)
    }

    private fun makePictureMemo(PATH: String) {
        setDynamicLL(binding.llDynamic, PATH, 2)
    }

    private fun makeTextMemo(PATH: String) {
        setDynamicLL(binding.llDynamic, PATH, 1)
    }
    //****동적 메모 할당

    //***Linear 레이아웃에 동적으로 레이아웃을 추가하거나 제거.
    private fun setDynamicLL(layout : LinearLayout, filepath : String, dflag : Int){
        val layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if(dflag == 1) {
            val containTView = layoutInflater.inflate(R.layout.addmemo_text_ll, null)
            layout.addView(containTView)

            val file = File(filepath)
            val inputStream = file.inputStream()
            val text = inputStream.bufferedReader().use{ it.readText() }

            val editText = containTView.findViewById<EditText>(R.id.EditDMemo)
            editText.setText(text)
            editText.clearFocus()

            containTView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                layout.removeView(containTView)
            }
        }
        else if(dflag == 2) {
            val containIView = layoutInflater.inflate(R.layout.addmemo_picture_ll, null)
            layout.addView(containIView)

            val file = File(filepath)
            val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
            val bitmap = ImageDecoder.decodeBitmap(decode)

            val imageView = containIView.findViewById<ImageView>(R.id.ImageMemo)
            imageView.setImageBitmap(bitmap)

            containIView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                layout.removeView(containIView)
            }
            containIView.findViewById<ImageView>(R.id.ImageFold).setOnClickListener {
                val imagememo = containIView.findViewById<ImageView>(R.id.ImageMemo)
                if(imagememo.visibility == View.VISIBLE){
                    imagememo.visibility = View.GONE
                } else{
                    imagememo.visibility = View.VISIBLE
                }
            }
        }
        else if(dflag == 3) {
            val containDView = layoutInflater.inflate(R.layout.addmemo_drawing_ll, null)
            layout.addView(containDView)

            val file = File(filepath)
            val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
            val bitmap = ImageDecoder.decodeBitmap(decode)

            val imageView = containDView.findViewById<ImageView>(R.id.DrawingMemo)
            imageView.setImageBitmap(bitmap)

            containDView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                layout.removeView(containDView)
            }
            containDView.findViewById<ImageView>(R.id.ImageFold).setOnClickListener {
                val imagememo = containDView.findViewById<ImageView>(R.id.DrawingMemo)
                if(imagememo.visibility == View.VISIBLE){
                    imagememo.visibility = View.GONE
                } else{
                    imagememo.visibility = View.VISIBLE
                }
            }
        }
    }
    //****

    //***DB에 하루치 모든 정보를 저장. 추후에 불러올때 다시 읽어들인다.
    private fun saveDBDay() {
        val dayDir = getExternalFilesDir(null).toString() + todayDate
        val file = File(dayDir)
        if(!file.exists()) {
            file.mkdirs()
        }
        Log.i("하루 주소", "${file.absolutePath}")
    }
    //***

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

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("time", mymemo)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        val notification = builder.build()
        manager.notify(10, notification)
    }
    //**


}
