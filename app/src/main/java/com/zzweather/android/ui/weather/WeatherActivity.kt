package com.zzweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zzweather.android.R
import com.zzweather.android.databinding.ActivityWeatherBinding
import com.zzweather.android.databinding.ForecastBinding
import com.zzweather.android.databinding.LifeIndexBinding
import com.zzweather.android.databinding.NowBinding
import com.zzweather.android.logic.model.Weather
import com.zzweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    public lateinit var id: ActivityWeatherBinding
    private lateinit var now: NowBinding
    private lateinit var fore: ForecastBinding
    private lateinit var life: LifeIndexBinding

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        ///改变的写法
        id = ActivityWeatherBinding.inflate(layoutInflater)
//        now = NowBinding.bind(id.root)
//        fore = ForecastBinding.bind(id.root)
//        life = LifeIndexBinding.bind(id.root)
        now = NowBinding.inflate(layoutInflater)
        fore = ForecastBinding.inflate(layoutInflater)
        life = LifeIndexBinding.inflate(layoutInflater)
        setContentView(id.root)
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
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            id.swipeRefresh.isRefreshing = false
        })
        id.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        id.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        now.navBtn.setOnClickListener {
            id.drawerLayout.openDrawer(GravityCompat.START)
        }
        id.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        id.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        now.currentTemp.text = currentTempText
        now.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        now.currentAQI.text = currentPM25Text
        now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据
        fore.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                fore.forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            fore.forecastLayout.addView(view)
        }
        //填充life_index布局中的数据
        val lifeIndex = daily.lifeIndex
        life.coldRiskText.text = lifeIndex.coldRisk[0].desc
        life.dressingText.text = lifeIndex.dressing[0].desc
        life.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        life.carWashingText.text = lifeIndex.carWashing[0].desc
        id.weatherLayout.visibility = View.VISIBLE
    }
}