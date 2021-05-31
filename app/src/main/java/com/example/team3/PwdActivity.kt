package com.example.team3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.team3.databinding.ActivityPwdBinding

class PwdActivity : AppCompatActivity() {
    lateinit var binding : ActivityPwdBinding
    var mode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userTheme = MyApplication.prefs.getString("theme", "default")
        when (userTheme){
            "default"->setTheme(R.style.DefaultTheme)
            "light"->setTheme(R.style.LightTheme)
            "dark"->setTheme(R.style.DarkTheme)
            else->setTheme(R.style.DefaultTheme)
        }

        binding = ActivityPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        var mode = intent.getIntExtra("mode",0)
        Log.d("확인", "모드는 "+mode)
        binding.apply {
            when (mode){
                1->{
                    textView2.text = "비밀번호 설정"
                }
                2->{
                    textView2.text = "비밀번호 확인"
                    btnSet.text = "확인"
                }
            }
            btnSet.setOnClickListener {
                if (pwd1.text != null && pwd2.text != null && pwd3.text != null && pwd4.text != null){
                    when (mode){
                        1->{
                            setPwd()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        2->{
                            Log.d("확인","진입")
                            if (checkPwd()){
                                clearEditText()
                                //textView2.text = "비밀번호 변경"
                                //mode = 1
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }else{
                                Toast.makeText(this@PwdActivity, "비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
                                clearEditText()
                            }
                        }
                    }
                }else{
                    Toast.makeText(this@PwdActivity, "4자리를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearEditText(){
        binding.apply {
            pwd1.text.clear()
            pwd2.text.clear()
            pwd3.text.clear()
            pwd4.text.clear()
        }
    }

    private fun setPwd(){
        binding.apply {
            val pwd = pwd1.text.toString() + pwd2.text.toString() + pwd3.text.toString() + pwd4.text.toString()
            MyApplication.prefs.setString("password",pwd)
        }
        Log.d("확인", "비밀번호는 "+ MyApplication.prefs.getString("password", ""))
    }

    private fun checkPwd():Boolean {
        Log.d("확인", "비밀번호는 "+ MyApplication.prefs.getString("password", ""))
        binding.apply {
            val pwd = pwd1.text.toString() + pwd2.text.toString() + pwd3.text.toString() + pwd4.text.toString()
            Log.d("확인", "입력: " + pwd)
            if (pwd == MyApplication.prefs.getString("password", ""))
                return true
            else
                return false
        }
    }
}