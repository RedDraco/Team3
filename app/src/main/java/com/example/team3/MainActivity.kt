package com.example.team3

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.team3.databinding.ActivityMainBinding



open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.navigationView.itemIconTintList = null
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val intent = Intent(this, SettingActivity::class.java)
        binding.apply {
            imageMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            settingBtn.setOnClickListener {
                startActivity(intent)
            }
        }

        //--------------------------------------임시 코드--------------------------------------------//
       // val intent2 = Intent(this, AddMemo::class.java).putExtra("path", "/storage/emulated/0/Android/data/com.example.team3/files/20210529_1221845715705003048.txt")
        val intent2 = Intent(this, AddMemo::class.java).putExtra("path", "/storage/emulated/0/Android/data/com.example.team3/files/Pictures/20210529_7369896620702851193.jpg")
        //val intent2 = Intent(this, AddMemo::class.java).putExtra("path", "/storage/emulated/0/Android/data/com.example.team3/files/Pictures/20210529_2628056321616605660.png")
        startActivityForResult(intent2,59)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        ) // requestCode : 어느 Intent에 대해서 넘어온 것인지 확인가능.  if(requestCode==123){
        if(resultCode == RESULT_OK){
            Log.i("인텐트 답장", "${data?.getStringExtra("path")}")
        }else{

        }
    }

    //--------------------------------------임시 코드--------------------------------------------//
}