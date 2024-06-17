package com.clockworkorange.shou.ui.tab.main.base

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.IShouPlace
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ListitemCouponPlaceBinding
import com.clockworkorange.shou.ext.loadImage
import com.clockworkorange.shou.util.IShouPlaceDiffUtil

class PlaceAdapter(private val listener: FavoriteActionListener<IShouPlace>): ListAdapter<IShouPlace, PlaceAdapter.ViewHolder>(IShouPlaceDiffUtil) {

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

    fun findStorePosition(storeId: Int): Int{
        return currentList.indexOfFirst { it.placeId == storeId }
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
        private var place: IShouPlace? = null

        init {
            binding.viewBgCoupon.setOnClickListener {
                listener.onItemClick(getItem(bindingAdapterPosition))
            }

            binding.flHeart.setOnClickListener {
                val heartColorResId = if (place?.placeId in favIds) R.color.color_unhighlight else R.color.color_highlight2
                binding.ivHeart.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, heartColorResId))
                listener.onFavoriteClick(getItem(bindingAdapterPosition))
            }
        }

        fun bind(place: IShouPlace){
            this.place = place
            with(binding){
                tvTitle.text = place.name
                ivCouponImage.loadImage(place.image)
                viewDash.isVisible = false
                tvDesc.isVisible = false
                val heartColorResId = if (place.placeId in favIds) R.color.color_highlight2 else R.color.color_unhighlight
                ivHeart.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, heartColorResId))

                val bgResId =  if (place.placeId == selectedId){
                    R.drawable.bg_round_8_a6a6a6
                }else{
                    R.drawable.bg_round_8_ffffff
                }
                viewBgCoupon.setBackgroundResource(bgResId)

            }
        }

    }
}