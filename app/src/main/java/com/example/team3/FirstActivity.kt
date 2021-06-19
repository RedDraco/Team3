package com.example.team3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team3.databinding.ActivityFirstBinding
import com.example.team3.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.schedule

class FirstActivity : AppCompatActivity() {
    lateinit var binding: ActivityFirstBinding
    val CHECK_PWD_REQUEST = 537

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

        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    fun init(){
        Timer().schedule(2000){
            if (MyApplication.prefs.getString("lock","false") == "true"){
                val intent = Intent(this@FirstActivity, PwdActivity::class.java)
                intent.putExtra("mode", 2)
                startActivityForResult(intent, CHECK_PWD_REQUEST)
            }
            else{
                val intent = Intent(this@FirstActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CHECK_PWD_REQUEST->{
                if (resultCode == Activity.RESULT_OK){
                    val intent = Intent(this@FirstActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}