package com.clockworkorange.shou.ui.tab.main.base

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.IShouCoupon
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ListitemCouponPlaceBinding
import com.clockworkorange.shou.ext.loadImage
import com.clockworkorange.shou.util.IShouCouponDiffUtil

class CouponAdapter(private val listener: FavoriteActionListener<IShouCoupon>): ListAdapter<IShouCoupon, CouponAdapter.ViewHolder>(IShouCouponDiffUtil) {

    private val favIds: MutableList<Int> = mutableListOf()

    var selectedId: Int = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setFavIds(ids: List<Int>) {
        favIds.clear()
        favIds.addAll(ids)
        notifyDataSetChanged()
    }

    fun findCouponPosition(storeId: Int): Int{
        return currentList.indexOfFirst { it.place.placeId == storeId }
    }

    fun clickOnPosition(position: Int){
        listener.onItemClick(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemCouponPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListitemCouponPlaceBinding): RecyclerView.ViewHolder(binding.root){

        private var coupon: IShouCoupon? = null
        init {
            binding.viewBgCoupon.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }

            binding.flHeart.setOnClickListener {
                val heartColorResId = if (coupon?.couponId in favIds) R.color.color_unhighlight else R.color.color_highlight2
                binding.ivHeart.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, heartColorResId))
                listener.onFavoriteClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(coupon: IShouCoupon){
            this.coupon = coupon
            with(binding){
                tvTitle.text = coupon.place.name
                ivCouponImage.loadImage(coupon.image)
                viewDash.isVisible = true
                tvDesc.isVisible = true
                tvDesc.text = coupon.name
                val heartColorResId = if (coupon.couponId in favIds) R.color.color_highlight2 else R.color.color_unhighlight
                ivHeart.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, heartColorResId))


                val bgResId =  if (coupon.couponId == selectedId){
                    R.drawable.bg_round_8_a6a6a6
                }else{
                    R.drawable.bg_round_8_ffffff
                }
                viewBgCoupon.setBackgroundResource(bgResId)

            }
        }

    }
}