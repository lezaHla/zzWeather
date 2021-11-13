package com.zzweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class ZzWeatherApplication: Application() {
    companion object{
        const val TOKEN = "G8PRC6B8LDbJoRG7"//在彩云天气申请到的令牌值，暂时未通过申请
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}