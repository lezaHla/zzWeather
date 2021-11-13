package com.zzweather.android.ui.place

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzweather.android.databinding.PlaceItemBinding
import com.zzweather.android.logic.model.Place
import com.zzweather.android.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: PlaceItemBinding) : RecyclerView.ViewHolder(view.root) {
        val placeName: TextView = view.placeName
        val placeAddress: TextView = view.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(view)
        // 给place_item.xml的最外层布局注册点击时间监听器, 在点击事件中获取当前点击项的经纬度坐标和地区名称
        holder.itemView.setOnClickListener {
            Log.d("PlaceAdapter","Debug")
            val position = holder.adapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            // 对PlaceFragment所处Activity进行判断
            // 如果在WeatherActivity中, 就关闭滑动菜单, 给WeatherViewModel赋值新的经纬度坐标和地区名称, 然后刷新天气
            // 如果是在MainActivity中, 那么就保持之前的处理逻辑不变
            if (activity is WeatherActivity) {
                activity.id.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                // 将获取到的经纬度坐标和地区名称转入到Intent中
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                // 启动WeatherActivity
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size

}