package com.clockworkorange.shou.ui.tab.main.couponlistbyplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.shou.databinding.ListitemTitleBinding
import com.clockworkorange.shou.util.AdapterListener

class CouponTitleAdapter(val listener: AdapterListener<ShouCoupon>): RecyclerView.Adapter<CouponTitleAdapter.ViewHolder>() {

    val data: ArrayList<ShouCoupon> = ArrayList()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemTitleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val binding: ListitemTitleBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(data[bindingAdapterPosition])
            }
        }

        fun bind(coupon: ShouCoupon) {
            binding.tvTitle.text = coupon.name
        }
    }
}