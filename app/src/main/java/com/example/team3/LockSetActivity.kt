package com.example.team3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.team3.databinding.ActivityLockSetBinding

class LockSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityLockSetBinding
    var flag = true
    val CHANGE_PWD_REQUEST = 100
    val CHECK_PWD_REQUEST = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityLockSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            CHANGE_PWD_REQUEST->{
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "비밀번호 설정 성공", Toast.LENGTH_SHORT).show()
                }
            }
            CHECK_PWD_REQUEST->{
                if (resultCode == Activity.RESULT_OK){
                    Log.d("확인", "비밀번호 확인 성공")
                    val intent = Intent(this, PwdActivity::class.java)
                    intent.putExtra("mode", 1)
                    startActivityForResult(intent, CHANGE_PWD_REQUEST)
                }
            }
        }
    }

    private fun init() {
        binding.apply {
            if (MyApplication.prefs.getString("password","") != ""){
                switch1.isChecked = true
                setLayout.visibility = View.VISIBLE
            }
            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    setLayout.visibility = View.VISIBLE
                    if (flag == true){
                        //비밀번호 설정 화면 띄우기
                        val intent = Intent(this@LockSetActivity, PwdActivity::class.java)
                        intent.putExtra("mode", 1)//mode1: 비밀번호 설정
                        startActivity(intent)
                        flag = false
                    }
                }else{
                    setLayout.visibility = View.INVISIBLE
                    flag = true
                }
            }

            changePwd.setOnClickListener {
                val intent = Intent(this@LockSetActivity, PwdActivity::class.java)
                intent.putExtra("mode", 2)//mode2: 비밀번호 변경
                startActivityForResult(intent, CHECK_PWD_REQUEST)
            }
            switch2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){

                }
            }
        }
    }
}