package com.example.team3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.team3.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat

import java.text.SimpleDateFormat
import java.util.*

open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var navigationView: NavigationView
    var selectedMonth: String = ""
    var selectedYear: String = ""
    private val url = "http://api.openweathermap.org/data/2.5/weather"
    private val appId = "b724ce4365af9091165c61e07361fb8e"
    var df = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userTheme = MyApplication.prefs.getString("theme", "default")
        Log.i("확인", userTheme)
        when (userTheme){
            "default"->setTheme(R.style.DefaultTheme)
            "light"->setTheme(R.style.LightTheme)
            "dark"->setTheme(R.style.DarkTheme)
            "pink"->setTheme(R.style.PinkTheme)
            "purple"->setTheme(R.style.PurpleTheme)
            "brown"->setTheme(R.style.BrownTheme)
            else->setTheme(R.style.DefaultTheme)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.navigationView.itemIconTintList = null
        setContentView(binding.root)
        navigationView = findViewById<NavigationView>(R.id.navigationView)
        init()
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun init() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        val calendarIntent = Intent(this, MainActivityC::class.java)

        binding.apply {
            imageMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            settingBtn.setOnClickListener {
                startActivity(settingIntent)
            }
            calendarView3.setOnDateChangeListener { view, year, month, dayOfMonth ->
                selectedYear= year.toString()
                calendarIntent.putExtra("year", year.toString())

                selectedMonth = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                calendarIntent.putExtra("month", selectedMonth)

                val DAY = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                calendarIntent.putExtra("day", DAY)

                startActivity(calendarIntent)

            }
            getWeatherBtn.setOnClickListener {
                val city = editCity!!.text.toString().trim { it <= ' ' }
                if (city == "") {
                    Toast.makeText(applicationContext, "도시를 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    var tempUrl = "$url?q=$city&appid=$appId"
                    val stringRequest =
                        StringRequest(Request.Method.POST, tempUrl,
                            { response -> //Log.d("response", response);
                                var output = ""
                                try {
                                    val jsonResponse = JSONObject(response)
                                    val jsonArray = jsonResponse.getJSONArray("weather")
                                    val jsonObjectWeather = jsonArray.getJSONObject(0)
                                    val description = jsonObjectWeather.getString("description")
                                    val icon = jsonObjectWeather.getString("icon")
                                    val jsonObjectMain = jsonResponse.getJSONObject("main")
                                    val temp = jsonObjectMain.getDouble("temp") - 273.15
                                    val cityName = jsonResponse.getString("name")
                                    val iconUrl = "http://openweathermap.org./img/wn/$icon@2x.png"
                                    weatherText.text = description
                                    tempText.text = df.format(temp) + "°C"
                                    cityNameText.text = cityName
                                    Glide.with(applicationContext).load(iconUrl)
                                        .into(weatherImageView)

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }) { error ->
                            Toast.makeText(
                                applicationContext,
                                error.toString().trim(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
                    requestQueue.add(stringRequest)
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val showAllIntent = Intent(this, ShowAllActivity::class.java)

        binding.apply {
            when(item.itemId){
                R.id.nav_explain -> {
                    if(selectedYear == "" && selectedMonth == ""){
                        var calendar: Calendar = Calendar.getInstance()
                        var dateFormat = SimpleDateFormat("yyyy-MM")
                        var date = dateFormat.format(calendar.time)
                        showAllIntent.putExtra("date", date)
                        startActivity(showAllIntent)
                    }else{
                        var date = "$selectedYear-$selectedMonth"
                        showAllIntent.putExtra("date", date)
                        startActivity(showAllIntent)
                    }
                }
            }
        }
        return false
    }

    override fun onBackPressed() {
        binding.apply {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers()
            } else {
                super.onBackPressed()
            }
        }

    }
}


