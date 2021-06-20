package com.example.team3

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.team3.databinding.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

class MainActivityC() : AppCompatActivity() {
    private var alarmMgr: AlarmManager?= null
    var iconList = arrayListOf<IconData>()
    lateinit var binding: ActivityMaincBinding
    lateinit var iconBinding:IcondlgBinding
    lateinit var iconAdapter: IconAdapter
    var alarmDBHelper = AlarmDBHelper(this, "alarmDB.db")
    var temp_priority = MyApplication.prefs.getString("priority", "default")//이전 priority 값을 알아야 나중에 cancel할 때 intent 파악 가능

    var tempContainView:View ?= null
    var ismodify:Boolean = false

    //*아이콘 관련 변수
    var iconId = 0
    var icon_flag = 0
    var icon_temp = 0
    //
    var ADD_REQUEST = 0
    var todayDate = "" //20210610 형태
    var file_flag = 1 //추가한 memo의 serial# -> filelist를 sort하고, 만들어진 레이아웃 순서 기억 용도.
    var memo_flag = 0 //추가한 memo의 갯수 -> 0이 되면 file_flag를 1로 다시 초기화 해야한다.
    //전 액티비티에서 받아오는 날짜 정보
    var YEAR = ""
    var MONTH = ""
    var DAY = ""
    //ADDMEMO에서 받아오는 파일 디렉토리
    var PATH = ""
    //하루 디렉토리 이름.
    var dayDir = ""

    //**알람 관련 변수들
    var Alarm_Hour = -1
    var Alarm_Min = -1
    var myampm = ""
    var myhour = ""
    var mymin = ""
    var message = " "
    //**

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

        binding = ActivityMaincBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent
        if(i.hasExtra("year") && i.hasExtra("month") && i.hasExtra("day")) {
            YEAR = i.getStringExtra("year")?:""
            MONTH = i.getStringExtra("month")?:""
            DAY = i.getStringExtra("day")?:""
            todayDate = YEAR + MONTH + DAY

            Log.i("todayDate", "$todayDate")
        }
        initDir() //하루 디렉토리 관련
        initDay() //저장된 하루를 다시 불러오기

        initIcon() //아이콘 데이터 관련
        init()
    }

    //**하루 디렉토리 및 txt, jpg, png 디렉토리 생성.
    private fun initDir(){
        dayDir = getExternalFilesDir(null).toString() + "/" + todayDate
        val dayfile = File(dayDir)
        //
        if(!dayfile.exists()) {
            dayfile.mkdirs()
            Log.i("initDir : 하루 디렉토리 생성", "$dayDir")
        }
    }
    //**

    private fun initDay(){
        val file = File(dayDir)
        val filelist = file.listFiles()
        Arrays.sort(filelist)
        if(filelist.size == 0) {
            //처음 여는 하루일때만, 기본 정보를 담는 txt파일 생성
            initFile()
        } else{
            //기본 txt 파일을 \n로 구분.
            val inputStream = filelist[0].inputStream()
            val datatext = inputStream.bufferedReader().use{ it.readText() }
            val splitString = datatext.split('\n')
            //
            file_flag = splitString[0].toInt()
            if(splitString[1]==" "){

            }else {
                message = splitString[1]
                binding.TextAlarm.setText(message)
            }
            //
            icon_flag = splitString[2].toInt()
            setIcon(icon_flag)
            //
            binding.EditTitle.setText(splitString[3])
            //추가 memo의 레이아웃들도 추가.
            if(filelist.size>=2) {
                for (i in 1 until filelist.size) {
                    val memoFilePath = filelist[i].absolutePath
                    decideExtra(memoFilePath, 0)
                    memo_flag++
                }
            }
        }
    }

    private fun initFile(){ //파일의 기본적인 정보들을 저장하기 위한 기본 txt파일
        val FileName = todayDate + "_0_data_"
        val storageDir = this.getExternalFilesDir(null)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val initfile = File.createTempFile(FileName, ".txt", storageDir)
        val sourcePath = Paths.get(getExternalFilesDir(null).toString() + "/" + initfile.name)
        val targetPath = Paths.get(dayDir + "/" + initfile.name)
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        Log.i("initFile : 하루 기본 파일 생성", "${File(targetPath.toString()).name}")
    }

    private fun initIcon(){
        iconList.add(IconData("icon_0"))
        iconList.add(IconData("icon_1"))
        iconList.add(IconData("icon_2"))
        iconList.add(IconData("icon_3"))
        iconList.add(IconData("icon_4"))
        iconList.add(IconData("icon_5"))
    }

    private fun initIconRecycler(){
        iconBinding.iconRecycler.layoutManager =  GridLayoutManager(this, 3)
        iconAdapter = IconAdapter(this, iconList)

        iconAdapter.itemClickListener = object :IconAdapter.OnItemClickListener{

            override fun OnItemClick(holder: IconAdapter.ViewHolder, view: View, iconData: IconData, position: Int) {
                //아이콘을 클릭하면 해당 아이콘 정보만 받아온다.
                iconId = resources.getIdentifier(iconList[position].photo, "drawable", packageName)
                icon_temp = position
            }

        }
        iconBinding.iconRecycler.adapter = iconAdapter
    }

    private fun init(){
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager


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
                    binding.ImageIcon.setImageResource(iconId)
                    icon_flag = icon_temp
                }
                .setNegativeButton("취소") { _, _ -> }
                .show()
        }
        //****아이콘 버튼****

        //****날짜 버튼**** -> 뒤로가기 기능.
        binding.TextDate.setOnClickListener {
            saveDBDay()
            this.finish()
        }
        //****날짜 버튼****

        //*****추가버튼*****
        binding.Plusbtn.setOnClickListener {
            ismodify = false
            //여기다가 하루 추가 액티비티 연결.
            val intent = Intent(this, AddMemo::class.java)
            intent.putExtra("date", todayDate)
            intent.putExtra("fileflag", file_flag)
            startActivityForResult(intent, ADD_REQUEST)
        }
        //*****추가버튼*****

        //*****알람*****
        binding.apply {
            if (alarmDBHelper.getID(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH) != -1){
                AlarmSwitch.isChecked = true}
            else {
                Alarm_Hour = -1
                Alarm_Min = -1
                AlarmSwitch.isChecked = false
                TextAlarm.setText("추가하기")
            }
        }
        binding.TextAlarm.setOnClickListener {
            //**푸쉬알람 설정
            setNotfTime()
        }
        binding.ImageAlarm.setOnClickListener {
            cancelAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
            Alarm_Hour = -1
            Alarm_Min = -1
            binding.AlarmSwitch.isChecked = false
            binding.TextAlarm.setText("추가하기")
            message = " "
        }
        binding.AlarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                if (Alarm_Hour < 0 || Alarm_Min < 0) { }
                else {
                    setAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Alarm_Hour, Alarm_Min)
                }
            }
            if(isChecked == false) {
                if (Alarm_Hour < 0 || Alarm_Min < 0) { }
                else {
                    cancelAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
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
                        file_flag = data.getIntExtra("fileflag", file_flag)
                        //val file = File(PATH)
                        decideExtra(PATH, 1)
                        memo_flag++
                    }
                }
            }
        }
    }
    //**

    //****AddMemo 액티비티에서 전달받은 memoPath의 확장자 확인
    private fun decideExtra(PATH : String, initflag: Int) {
        if(PATH == "")
            return
        else{
            val splitString = PATH.split('.')
            val extension = splitString.last()

            //*확장자에 따라 FrameLayout에 동적으로 메모 추가.
            when(extension){
                "txt" -> setDynamicLL(binding.llDynamic, PATH, 1, initflag)
                "jpg" -> setDynamicLL(binding.llDynamic, PATH, 2, initflag)
                "png" -> setDynamicLL(binding.llDynamic, PATH, 3, initflag)
                else ->  return
            }
        }
    }
    //****

    //***Linear 레이아웃에 동적으로 레이아웃을 추가하거나 제거.
    private fun setDynamicLL(layout : LinearLayout, filepath : String, dflag : Int, initflag : Int){
        val layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if(dflag == 1) { //Text
            val containTView = layoutInflater.inflate(R.layout.addmemo_text_ll, null)
            layout.addView(containTView)

            val file = File(filepath)
            val inputStream = file.inputStream()
            val text = inputStream.bufferedReader().use{ it.readText() }

            val sourcePath = Paths.get(filepath)
            val targetPath = Paths.get(dayDir + "/" + file.name)
            if(initflag==1) {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                Log.i("setDynamicll", "${File(targetPath.toString()).absolutePath}")
            }

            val editText = containTView.findViewById<EditText>(R.id.EditDMemo)
            editText.setText(text)
            editText.clearFocus()

            containTView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                //뷰에서 remove하고 file도 삭제
                layout.removeView(containTView)
                File(targetPath.toString()).delete()
                memo_flag--
                if(memo_flag==0)
                    file_flag = 1
            }
            containTView.findViewById<ImageView>(R.id.ImageFold).setOnClickListener {
                val textmemo = containTView.findViewById<EditText>(R.id.EditDMemo)
                if(textmemo.visibility == View.VISIBLE){
                    textmemo.visibility = View.GONE
                } else{
                    textmemo.visibility = View.VISIBLE
                }
            }
            containTView.findViewById<EditText>(R.id.EditDMemo).setOnClickListener {
                tempContainView = containTView
                ismodify = true

                val intent = Intent(this, AddMemo::class.java)
                intent.putExtra("date", todayDate)
                intent.putExtra("fileflag", file_flag)
                intent.putExtra("path", targetPath.toString())
                startActivityForResult(intent, ADD_REQUEST)
            }
        }
        else if(dflag == 2) {
            val containIView = layoutInflater.inflate(R.layout.addmemo_picture_ll, null)
            layout.addView(containIView)

            val file = File(filepath)
            val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
            val bitmap = ImageDecoder.decodeBitmap(decode)

            val sourcePath = Paths.get(filepath)
            val targetPath = Paths.get(dayDir + "/" + file.name)
            if(initflag==1) {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                Log.i("setDynamicll", "${File(targetPath.toString()).absolutePath}")
            }

            val imageView = containIView.findViewById<ImageView>(R.id.ImageMemo)
            imageView.setImageBitmap(bitmap)

            containIView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                //뷰에서 remove하고 file도 삭제
                layout.removeView(containIView)
                File(targetPath.toString()).delete()
                memo_flag--
                if(memo_flag==0)
                    file_flag = 1
            }
            containIView.findViewById<ImageView>(R.id.ImageFold).setOnClickListener {
                val imagememo = containIView.findViewById<ImageView>(R.id.ImageMemo)
                if(imagememo.visibility == View.VISIBLE){
                    imagememo.visibility = View.GONE
                } else{
                    imagememo.visibility = View.VISIBLE
                }
            }
            containIView.findViewById<ImageView>(R.id.ImageMemo).setOnClickListener {
                tempContainView = containIView
                ismodify = true

                val intent = Intent(this, AddMemo::class.java)
                intent.putExtra("date", todayDate)
                intent.putExtra("fileflag", file_flag)
                intent.putExtra("path", targetPath.toString())
                startActivityForResult(intent, ADD_REQUEST)
            }
        }
        else if(dflag == 3) {
            val containDView = layoutInflater.inflate(R.layout.addmemo_drawing_ll, null)
            layout.addView(containDView)

            val file = File(filepath)
            val decode = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
            val bitmap = ImageDecoder.decodeBitmap(decode)

            val sourcePath = Paths.get(filepath)
            val targetPath = Paths.get(dayDir + "/" + file.name)
            if(initflag==1) {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                Log.i("setDynamicll", "${File(targetPath.toString()).absolutePath}")
            }

            val imageView = containDView.findViewById<ImageView>(R.id.DrawingMemo)
            imageView.setImageBitmap(bitmap)

            containDView.findViewById<ImageView>(R.id.ImageTrash).setOnClickListener {
                //뷰에서 remove하고 file도 삭제
                layout.removeView(containDView)
                File(targetPath.toString()).delete()
                memo_flag--
                if(memo_flag==0)
                    file_flag = 1
            }
            containDView.findViewById<ImageView>(R.id.ImageFold).setOnClickListener {
                val imagememo = containDView.findViewById<ImageView>(R.id.DrawingMemo)
                if(imagememo.visibility == View.VISIBLE){
                    imagememo.visibility = View.GONE
                } else{
                    imagememo.visibility = View.VISIBLE
                }
            }
            containDView.findViewById<ImageView>(R.id.DrawingMemo).setOnClickListener {
                tempContainView = containDView
                ismodify = true

                val intent = Intent(this, AddMemo::class.java)
                intent.putExtra("date", todayDate)
                intent.putExtra("fileflag", file_flag)
                intent.putExtra("path", targetPath.toString())
                startActivityForResult(intent, ADD_REQUEST)
            }
        }
        Log.i("filepath", "$filepath")
        if(initflag == 1 && ismodify){
            ismodify = false
            Log.i("delete", "delete view")
            layout.removeView(tempContainView)
            memo_flag--
            if(memo_flag==0)
                file_flag = 1
        }

    }
    //****

    //***DB에 하루치 모든 정보를 저장. 추후에 불러올때 다시 읽어들인다.
    private fun saveDBDay() {
        val file1 = File(dayDir)
        val filelist1 = file1.listFiles()
        Arrays.sort(filelist1)
        filelist1[0].delete()
        initFile()

        val file2 = File(dayDir)
        val filelist2 = file2.listFiles()
        Arrays.sort(filelist2)

        val fout = FileOutputStream(filelist2[0]!!)
        val writer = PrintWriter(fout)
        //쓰기 시작.
        writer.println(file_flag.toString()) //추가 memo 순서 기억 용도 -> Int로 변환 후 사용
        writer.println(message) //알람은 유지되지만, 알람 text를 설정하기 위한 용도.
        writer.println(icon_flag.toString()) //icon 기억 용도 -> Int로 변환 후 사용
        writer.println(binding.EditTitle.text.toString()) //EditTitle 불러오기 위한 용도.

        writer.close()
        fout.close()
    }
    //***

    //****알람 관련 함수들
    fun setNotfTime(){//timepickerdialog 띄우기 -> 선택하면 setAlarm 호출
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{
                view: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            //****
            myhour = view!!.hour.toString()
            mymin = view!!.minute.toString()
            //오전 오후 설정
            if (myhour.toInt() >= 12 && myhour.toInt() <= 24) {
                myampm = "오후"
            } else {
                myampm = "오전"
            }

            if (myhour.toInt() >= 0 && myhour.toInt() <= 9)
                myhour = "0" + myhour

            if (mymin.toInt() >= 0 && mymin.toInt() <= 9)
                mymin = "0" + mymin
            //알람 옆에 텍스트 설정
            message = myampm + " " + myhour + " : " + mymin
            binding.TextAlarm.setText(message)
            //스위치 온
            binding.AlarmSwitch.isChecked = true
            //****
            Alarm_Hour = view!!.hour
            Alarm_Min = view!!.minute
            setAlarm(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hour, minute)
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
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
        MyApplication.prefs.setInt("cnt",cnt+1)
        Toast.makeText(this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(year:Int, month:Int, day:Int){//알람 취소
        lateinit var intent: Intent
        when (temp_priority){
            "high"-> intent = Intent(applicationContext, ReceiverHigh::class.java)
            "default"-> intent = Intent(applicationContext, ReceiverDefault::class.java)
            "low" -> intent = Intent(applicationContext, ReceiverLow::class.java)
            "min" -> intent = Intent(applicationContext, ReceiverMin::class.java)
        }
        val alarmId = alarmDBHelper.getID(year, month, day)
        if (alarmId > -1){
            val alarmIntent = PendingIntent.getBroadcast(applicationContext, alarmId, intent, PendingIntent.FLAG_NO_CREATE)
            if (alarmIntent != null) {
                alarmMgr?.cancel(alarmIntent)
                alarmDBHelper.deleteAlarm(alarmId)
            }
        }
        val allAlarms = alarmDBHelper.getAllRecord()
        Toast.makeText(this, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }
    //****알람 관련 함수들

    //*초기화할때, 이미지 아이콘 정보 바꾸는 함수.
    fun setIcon(iconNum:Int){
        if(iconNum==0) {
            binding.ImageIcon.setImageResource(R.drawable.icon_0)
        }
        else if(iconNum==1) {
            binding.ImageIcon.setImageResource(R.drawable.icon_1)
        }
        else if(iconNum==2) {
            binding.ImageIcon.setImageResource(R.drawable.icon_2)
        }
        else if(iconNum==3) {
            binding.ImageIcon.setImageResource(R.drawable.icon_3)
        }
        else if(iconNum==4) {
            binding.ImageIcon.setImageResource(R.drawable.icon_4)
        }
        else if(iconNum==5) {
            binding.ImageIcon.setImageResource(R.drawable.icon_5)
        }
    }
    //*

    //뒤로 가기 키나, 앱 강제 종료시 자동 저장하기 위해, onPause때 저장한다.
    override fun onPause() {
        saveDBDay()
        super.onPause()
        Log.i("MainActivityC", "Paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivityC", "Destroyed")
    }
}
