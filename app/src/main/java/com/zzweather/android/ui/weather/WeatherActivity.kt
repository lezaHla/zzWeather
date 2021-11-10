package com.zzweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zzweather.android.R
import com.zzweather.android.databinding.*
import com.zzweather.android.logic.model.Weather
import com.zzweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if(weather != null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    private fun showWeatherInfo(weather: Weather) {
        val bindingNow = NowBinding.inflate(layoutInflater)
        val bindingForecast = ForecastBinding.inflate(layoutInflater)
        val bindingForecastItem = ForecastItemBinding.inflate(layoutInflater)
        val bindingLifeIndex = LifeIndexBinding.inflate(layoutInflater)
        bindingNow.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        bindingNow.currentTemp.text = currentTempText
        bindingNow.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        bindingNow.currentAQI.text = currentPM25Text
        bindingNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据
        bindingForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                bindingForecast.forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dataInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.data)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            bindingForecast.forecastLayout.addView(view)
        }
        //填充life_index布局中的数据
        val lifeIndex = daily.lifeIndex
        bindingLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        bindingLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        bindingLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        bindingLifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        val weatherLayout: ScrollView = findViewById(R.id.weatherLayout)
    }
}