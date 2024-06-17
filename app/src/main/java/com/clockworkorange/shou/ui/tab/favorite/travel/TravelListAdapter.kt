package com.clockworkorange.shou.ui.tab.favorite.travel

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ListitemTravelBinding
import com.clockworkorange.shou.ui.tab.main.base.FavoriteActionListener
import com.clockworkorange.shou.util.ShouTravelDiffUtil
import java.text.SimpleDateFormat
import java.util.*

class TravelListAdapter(private val listener: FavoriteActionListener<ShouTravel>) : ListAdapter<ShouTravel, TravelListAdapter.ViewHolder>(ShouTravelDiffUtil) {

    private val sdfFrom = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val sdfTo = SimpleDateFormat("yyyy年MM月dd日")

    var selectedId: Int = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListitemTravelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListitemTravelBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.flHeart.setOnClickListener {
                listener.onFavoriteClick(getItem(bindingAdapterPosition))
            }

            binding.viewBg.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(shouTravel: ShouTravel) {
            binding.tvTitle.text = shouTravel.name
            val date = sdfFrom.parse(shouTravel.date)
            binding.tvDate.text = "建立日期 %s".format(sdfTo.format(date))

            if (shouTravel.travelId == selectedId){
                binding.viewBg.setBackgroundResource(R.drawable.bg_round_8_a6a6a6)
                binding.tvDate.setTextColor(Color.parseColor("#dcdddd"))
            }else{
                binding.viewBg.setBackgroundResource(R.drawable.bg_round_8_ffffff)
                binding.tvDate.setTextColor(Color.parseColor("#a6a6a6"))
            }

        }
    }
}