package com.clockworkorange.shou.ui.tab.favorite.travel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.databinding.ListitemSimpleWaypointBinding
import com.clockworkorange.shou.util.TravelWayPointDiffUtil

class SimpleWayPointAdapter: ListAdapter<TravelWayPoint, SimpleWayPointAdapter.ViewHolder>(TravelWayPointDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListitemSimpleWaypointBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListitemSimpleWaypointBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: TravelWayPoint) {
            with(binding){
                tvOrder.text = "%d".format(bindingAdapterPosition+1)
                tvTitle.text = item.name

                if (item.isNavParking){
                    tvDesc.text = "停車場"
                }else{
                    when(item){
                        is TravelWayPoint.TravelCoupon -> tvDesc.text = "優惠券"
                        is TravelWayPoint.TravelPlace -> tvDesc.text = "商店"
                    }
                }
            }

        }

    }
}