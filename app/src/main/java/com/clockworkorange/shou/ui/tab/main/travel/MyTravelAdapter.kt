package com.clockworkorange.shou.ui.tab.main.travel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.databinding.ListitemWaypointBinding


class MyTravelAdapter(private var listener: Listener? = null): RecyclerView.Adapter<MyTravelAdapter.ViewHolder>() {

    interface Listener {
        fun onDelete(place: TravelWayPoint)
        fun onTouchMoveOrder(viewHolder: ViewHolder)
    }

    private val data = mutableListOf<TravelWayPoint>()

    fun setData(list: List<TravelWayPoint>){
        data.clear()
        data.addAll(list)
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemWaypointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val binding: ListitemWaypointBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.viewClickDelete.setOnClickListener {
                listener?.onDelete(data[bindingAdapterPosition])
            }

            binding.viewClickMoveOrder.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) listener?.onTouchMoveOrder(this)
                false
            }

        }

        fun bind(wayPoint: TravelWayPoint){

            binding.tvOrder.text = "%d".format(bindingAdapterPosition+1)
            binding.tvTitle.text = wayPoint.name

            when(wayPoint){
                is TravelWayPoint.TravelCoupon -> binding.tvDesc.text = "優惠券"
                is TravelWayPoint.TravelPlace -> binding.tvDesc.text = "景點"
            }

            if (wayPoint.isNavParking){
                binding.tvDesc.text = "停車場"
                binding.tvTitle.text = "${wayPoint.name} 特約停車場"
            }

            binding.tvDistance.text = "%.1f 公里".format(wayPoint.distance)
        }
    }

}