package com.example.team3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import com.example.team3.databinding.ActivityAddMemoBinding
import com.google.android.material.tabs.TabLayout

class AddMemo : AppCompatActivity() {

    companion object{
        const val PICREQUEST = 100
        const val NEWMEMO = 10
        const val MODIFYTEXT = 11
        const val MODIFYPICTURE = 12
        const val MODIFYDRAWING = 13
    }

    lateinit var binding:ActivityAddMemoBinding
    var memoPath:String = ""
    var flag = 0
    var date = ""
    var file_flag = 0

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

        binding = ActivityAddMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = intent!!
        memoPath = i.getStringExtra("path")?:""
        date = i.getStringExtra("date")?:""
        file_flag = i.getIntExtra("fileflag", 0)
        decideFragment()
        init()
    }

    private fun decideFragment() {
        if(memoPath == "")
            flag = NEWMEMO
        else{

            val splitString = memoPath.split('.')
            val extension = splitString.last()

            when(extension){
                "txt" -> flag = MODIFYTEXT
                "jpg" -> flag = MODIFYPICTURE
                "png" -> flag = MODIFYDRAWING
                else -> flag = 0
            }
        }
    }

    private fun init() {

        if(flag == 0) {
            Log.i("flag", "올바르지 않은 확장자")
            finish()
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            Log.i("reqPermission", "do")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PICREQUEST)
        }


        binding.apply {

            when(flag){
                NEWMEMO -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MemoFragment(memoPath, flag, date, file_flag)).commit()
                MODIFYTEXT -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MemoFragment(memoPath, flag, date, file_flag)).commit()
                MODIFYPICTURE ->{
                    tabLayout.getTabAt(1)!!.select()
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, PictureFragment(memoPath, flag, date, file_flag)).commit()
                }
                MODIFYDRAWING -> {
                    tabLayout.getTabAt(2)!!.select()
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, DrawingFragment(memoPath, flag, date, file_flag)).commit()
                }
            }

            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position

                    when(position){
                        0 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MemoFragment(memoPath, flag, date, file_flag)).commit()
                        1 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, PictureFragment(memoPath, flag, date, file_flag)).commit()
                        2 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, DrawingFragment(memoPath, flag, date, file_flag)).commit()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PICREQUEST -> {
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED)) {

                    Toast.makeText(this, "권한 거절. 앱 설정에서 확인", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }
        }
    }
}