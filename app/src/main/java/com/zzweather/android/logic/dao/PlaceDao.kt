package com.zzweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.zzweather.android.ZzWeatherApplication
import com.zzweather.android.logic.model.Place

object PlaceDao {

    fun savePlace(place: Place){
        sharedPreferences().edit{
            putString("place",Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = ZzWeatherApplication.context.
    getSharedPreferences("zz_weather", Context.MODE_PRIVATE)
}