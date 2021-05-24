package com.example.team3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.team3.databinding.ActivityAddMemoBinding
import com.google.android.material.tabs.TabLayout

class AddMemo : AppCompatActivity() {
    lateinit var binding:ActivityAddMemoBinding
    val PICREQUEST = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
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


    private fun init() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            Log.i("reqPermission", "do")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PICREQUEST)
        }


        binding.apply {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MemoFragment()).commit()

            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position

                    when(position){
                        0 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MemoFragment()).commit()
                        1 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, PictureFragment()).commit()
                        2 -> supportFragmentManager.beginTransaction().replace(R.id.frameLayout, DrawingFragment()).commit()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }
}