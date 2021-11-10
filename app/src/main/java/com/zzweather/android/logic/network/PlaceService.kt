package com.zzweather.android.logic.network

import com.zzweather.android.ZzWeatherApplication
import com.zzweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


/*
    定义一个用于访问彩云天气城市搜索API的Retrofit接口
 */
interface PlaceService {
    @GET("v2/place?token=${ZzWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
    //返回值设为Call<PlaceResponse>，这样可以将服务器返回的JSON数据自动解析为PlaceResponse
}