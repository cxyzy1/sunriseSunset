package com.cxyzy.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sunriseTime = "08:00"
        var sunsetTime = "19:00"
        val dateFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
        val nowDate = Date(System.currentTimeMillis())
        val nowTime = dateFormat.format(nowDate)
        sunriseSunset.setTime(sunriseTime, sunsetTime, nowTime)
        sunriseSunset.startAnimation()
    }

}
