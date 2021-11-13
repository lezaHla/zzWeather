package com.zzweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zzweather.android.MainActivity
import com.zzweather.android.databinding.FragmentPlaceBinding
import com.zzweather.android.ui.weather.WeatherActivity

class PlaceFragment : Fragment() {

    private lateinit var id: FragmentPlaceBinding

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }  // 使用lazy函数, 懒加载技术来获取PlaceViewMode实例

    private lateinit var adapter: PlaceAdapter

    // Fragment的标准写法 用来加载布局
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id = FragmentPlaceBinding.inflate(inflater, container, false)
        return id.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 当PlaceFragment被嵌入MainActivity中, 并且之前已经存在选中的城市
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        id.recyclerView.layoutManager = layoutManager      // 设置LayoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)   //使用placeList集合作为数据源
        id.recyclerView.adapter = adapter          // 设置适配器
        id.searchPlaceEdit.addTextChangedListener { editable ->     //动态监听搜索框
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                id.recyclerView.visibility = View.GONE
                id.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                id.recyclerView.visibility = View.VISIBLE
                id.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}