package com.example.team3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.team3.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat


open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val url = "http://api.openweathermap.org/data/2.5/weather"
    private val appId = "b724ce4365af9091165c61e07361fb8e"
    var df = DecimalFormat("#.##")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.navigationView.itemIconTintList = null
        setContentView(binding.root)

        init()
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
                startActivity(calendarIntent)
                calendarIntent.putExtra("year", year)
                calendarIntent.putExtra("year", month)
                calendarIntent.putExtra("year", dayOfMonth)

            }
            getWeatherBtn.setOnClickListener {
                val city = editCity!!.text.toString().trim { it <= ' '}
                if(city==""){
                    Toast.makeText(applicationContext, "도시를 입력해주세요", Toast.LENGTH_SHORT).show()
                }else{
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
                                    Glide.with(applicationContext).load(iconUrl).into(weatherImageView)

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
    }


