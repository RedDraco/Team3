package com.example.team3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.team3.databinding.ActivityPwdBinding
import java.util.concurrent.Executor

class PwdActivity : AppCompatActivity() {

    lateinit var binding : ActivityPwdBinding
    var mode: Int = 0

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

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

        binding = ActivityPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        var mode = intent.getIntExtra("mode",0)
        Log.d("확인", "모드는 "+mode)//mode가 1이면 비밀번호 설정, 2이면 비밀번호 확인 화면 띄워야 함
        binding.apply {
            when (mode){
                1->{
                    textView2.text = "비밀번호 설정"
                    fingerPrintView.visibility = View.INVISIBLE
                }
                2->{
                    textView2.text = "비밀번호 확인"
                    fingerPrintView.visibility = View.VISIBLE
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
            fingerPrintView.setOnClickListener {
                initFingerPrint()
            }
        }
    }

    private fun initFingerPrint() {
        val biometricManager = BiometricManager.from(this)
        var fpIsEnabled = false
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->{
                fpIsEnabled = true
                Log.d("확인", "App can authenticate using biometrics.")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("확인", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("확인", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("확인", "지문 설정 필요")
            }
        }

        if (fpIsEnabled){
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@PwdActivity, "error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@PwdActivity, "인증 성공!", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@PwdActivity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("생체인식 인증")
                .setSubtitle("지문을 이용해 앱에 진입할 수 있습니다.")
                .setNegativeButtonText("취소")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }else Toast.makeText(this, "지문 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
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
    }

    private fun checkPwd():Boolean {
        binding.apply {
            val pwd = pwd1.text.toString() + pwd2.text.toString() + pwd3.text.toString() + pwd4.text.toString()
            if (pwd == MyApplication.prefs.getString("password", ""))
                return true
            else
                return false
        }
    }
}