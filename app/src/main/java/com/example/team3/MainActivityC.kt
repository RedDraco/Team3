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
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

class MainActivityC() : AppCompatActivity() {
    private var alarmMgr: AlarmManager?= null
    private lateinit var alarmIntent : PendingIntent
    var iconList = arrayListOf<IconData>()
    lateinit var binding: ActivityMaincBinding
    lateinit var iconBinding:IcondlgBinding
    lateinit var iconAdapter: IconAdapter

    //*아이콘 관련 변수
    var resourceId = 0
    var icon_flag = 0
    //*

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
    //하루 디렉토리 이름.
    var dayDir = ""
    var dayTextDir = ""
    var dayJpgDir = ""
    var dayPngDir = ""

    //**알람 관련 변수들
    var Alarm_Hour = -1
    var Alarm_Min = -1
    var myampm = ""
    var myhour = ""
    var mymin = ""
    var message = ""
    //**

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaincBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        if(i.hasExtra("year") && i.hasExtra("month") && i.hasExtra("day")) {
            YEAR = i.getStringExtra("year")?:""
            MONTH = i.getStringExtra("month")?:""
            DAY = i.getStringExtra("day")?:""
            todayDate = "/" + YEAR + MONTH + DAY

            Log.i("MainActivityC", "$todayDate")
        }
        initDir()

        initData()
        init()

    }

    //**하루 디렉토리 및 txt, jpg, png 디렉토리 생성.
    private fun initDir(){
        dayDir = getExternalFilesDir(null).toString() + todayDate
        val dayfile = File(dayDir)
        if(!dayfile.exists()) {
            dayfile.mkdirs()
            Log.i("하루 디렉토리", "생성 완료")
        }
        Log.i("하루 주소", "${dayfile.absolutePath}")
        dayTextDir = dayDir + "/" + "TEXT"
        val dayTextfile = File(dayTextDir)
        if(!dayTextfile.exists()) {
            dayTextfile.mkdirs()
            Log.i("TEXT 디렉토리", "생성 완료")
        }
        Log.i("TEXT 주소", "${dayTextfile.absolutePath}")
        dayJpgDir = dayDir + "/" + "JPG"
        val dayJpgfile = File(dayJpgDir)
        if(!dayJpgfile.exists()) {
            dayJpgfile.mkdirs()
            Log.i("JPG 디렉토리", "생성 완료")
        }
        Log.i("JPG 주소", "${dayJpgfile.absolutePath}")
        dayPngDir = dayDir + "/" + "PNG"
        val dayPngfile = File(dayPngDir)
        if(!dayPngfile.exists()) {
            dayPngfile.mkdirs()
            Log.i("PNG 디렉토리", "생성 완료")
        }
        Log.i("PNG 주소", "${dayPngfile.absolutePath}")
    }
    //**

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
                //아이콘을 클릭하면 해당 아이콘 정보만 받아온다.
                resourceId = resources.getIdentifier(iconList[position].photo, "drawable", packageName)
            }

        }
        iconBinding.iconRecycler.adapter = iconAdapter
    }

    private fun init(){

        //클릭한 날짜에 따라, 날짜 텍스트 설정
        binding.TextDate.setText(MONTH + "월 " + DAY + "일")


        //****아이콘 버튼****
        binding.ImageIcon.setOnClickListener {
            iconBinding = IcondlgBinding.inflate(layoutInflater)
            initIconRecycler()

            val icondlgBuilder = AlertDialog.Builder(this)

            icondlgBuilder.setView(iconBinding.root)
                .setPositiveButton("확인") { _, _ ->
                    //아이콘 설정은 확인을 누르면 한다.
                    binding.ImageIcon.setImageResource(resourceId)
                    iconBinding
                }
                .setNegativeButton("취소") { _, _ ->

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
        binding.TextAlarm.setOnClickListener {
            //**푸쉬알람 설정
            setNotfTime(-1, -1)

        }
        binding.ImageAlarm.setOnClickListener {
            unSetAlarm()
            Alarm_Hour = -1
            Alarm_Min = -1
            binding.AlarmSwitch.isChecked = false
            binding.TextAlarm.setText("추가하기")
        }
        binding.AlarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                if (Alarm_Hour < 0 || Alarm_Min < 0) { }
                else {
                    setAlarmTime(Alarm_Hour, Alarm_Min)
                }
            }
            if(isChecked == false) {
                if (Alarm_Hour < 0 || Alarm_Min < 0) { }
                else {
                    unSetAlarm()
                }
            }
        }
        //*****알람*****
    }

    //**액티비티를 돌려받음과 함께 Request정보도 같이 받음
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ADD_REQUEST->{
                if(resultCode == Activity.RESULT_OK){
                    if(data?.hasExtra("path")!!){
                        PATH = data.getStringExtra("path")!!
                        Log.i("MainActivityC", "$PATH")
                        //val file = File(PATH)
                        decideExtra(PATH)
                    }
                }
            }
        }
    }
    //**

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

        if(dflag == 1) { //Text
            val containTView = layoutInflater.inflate(R.layout.addmemo_text_ll, null)
            layout.addView(containTView)

            val file = File(filepath)
            val inputStream = file.inputStream()
            val text = inputStream.bufferedReader().use{ it.readText() }

            val sourcePath = Paths.get(getExternalFilesDir(null).toString() + "/" + file.name)
            val targetPath = Paths.get(dayTextDir + "/" + file.name)
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)

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

            val sourcePath = Paths.get(getExternalFilesDir(null).toString() + "/" + file.name)
            val targetPath = Paths.get(dayJpgDir + "/" + file.name)
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)

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

            val sourcePath = Paths.get(getExternalFilesDir(null).toString() + "/" + file.name)
            val targetPath = Paths.get(dayPngDir + "/" + file.name)
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)

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
        val dayfile = File(dayDir)
        if(!dayfile.exists()) {
            dayfile.mkdirs()
        }
        Log.i("하루 주소", "${dayfile.absolutePath}")
    }
    //***

    //****알람 관련 함수들
    fun setNotfTime(hour: Int, min:Int){
        //전에 저장된 알람이 없으면,
        if(hour < 0 || min < 0) {
            val cal = Calendar.getInstance()
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)
                    unSetAlarm()
                    //****
                    myhour = view!!.hour.toString()
                    mymin = view!!.minute.toString()
                    //오전 오후 설정
                    if (myhour.toInt() >= 12 && myhour.toInt() <= 24) {
                        myampm = "오후"
                    } else {
                        myampm = "오전"
                    }

                    if (myhour >= "0" && myhour <= "9") {
                        myhour = "0" + myhour
                    } else { }

                    if (mymin >= "0" && mymin <= "9") {
                        mymin = "0" + mymin
                    } else { }
                    //알람 옆에 텍스트 설정
                    message = myampm + " " + myhour + " : " + mymin
                    binding.TextAlarm.setText(message)
                    //스위치 온
                    binding.AlarmSwitch.isChecked = true
                    //****
                    Alarm_Hour = hourOfDay
                    Alarm_Min = minute
                    setAlarmTime(Alarm_Hour, Alarm_Min)
                }

            TimePickerDialog(
                this, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true
            ).show()

        } else{ //스위치를 통해 알람을 끈 상태라면,
            setAlarmTime(hour, min)
        }
    }

    private fun setAlarmTime(hour:Int, minute:Int){
        Log.d("확인", hour.toString() + "시 " + minute.toString())
        val calender: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, MyReceiver::class.java).let{
                intent-> PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.setInexactRepeating(//반복//반복할 필요 없으면 제거
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
        //toast 메시지 출력
        Toast.makeText(this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    }

    fun unSetAlarm() {
        alarmMgr?.cancel(alarmIntent)
        //toast 메시지 출력
        Toast.makeText(this, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }
    //****알람 관련 함수들

}
