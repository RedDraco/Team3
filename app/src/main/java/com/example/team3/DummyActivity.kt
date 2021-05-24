package com.example.calenderapp_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.team3.databinding.ActivityDummyBinding

class DummyActivity : AppCompatActivity() {
    lateinit var binding :ActivityDummyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDummyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        //**임시 - 리퀘스트 값을 무사히 보내주는지 확인
        binding.apply {
            button.setOnClickListener {
                //val intent = Intent()
                //intent.putExtra()
                setResult(1)
                finish()
            }
            button2.setOnClickListener {
                //val intent = Intent()
                //intent.putExtra()
                setResult(2)
                finish()
            }
        }
        //**임시
    }
}