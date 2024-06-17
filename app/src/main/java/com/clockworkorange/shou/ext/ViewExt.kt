package com.clockworkorange.shou.ext

import android.content.res.ColorStateList
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.*


fun ImageView.loadImage(url: String){
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ItemAddTravelButtonBinding.setDisable(isDisable: Boolean){
    tvAddTravel.text = if (isDisable) "已加入旅程" else "加入旅程"
    val travelBgResId = if (isDisable) R.drawable.bg_round_3e3a39 else R.drawable.bg_add_travel_button
    llAddTravel.setBackgroundResource(travelBgResId)
    llAddTravel.isClickable = !isDisable
}

fun ViewBinding.requireContext() = root.context

fun ViewBinding.getColor(@ColorRes id: Int) = requireContext().getColor(id)

fun ViewBinding.getDrawable(@DrawableRes id: Int) = requireContext().getDrawable(id)

fun ItemTopMenuBinding.refresh(){
    root.backgroundTintList = ColorStateList.valueOf(getColor(R.color.color_top_menu_tint))
    tvMore.setTextColor(getColor(R.color.color_primary))
    ivTriangle.setColorFilter(getColor(R.color.color_primary))
}

fun ItemTopMenuContentBinding.refresh(){
    root.background = getDrawable(R.drawable.bg_round_30_color_on_background)
    tvByArea.setTextColor(getColor(R.color.color_primary))
    ivByArea.setImageResource(R.drawable.ic_top_memu_select)
    tvByLocation.setTextColor(getColor(R.color.color_primary))
    ivByLocation.setImageResource(R.drawable.ic_top_memu_select)
}

fun ItemAddTravelButtonBinding.refresh(){
    root.background = getDrawable(R.drawable.bg_add_travel_button)
}

fun ItemStoreInfoBinding.refresh(){
    root.background = getDrawable(R.drawable.bg_add_travel_button)
}

fun ItemStartRouteDaynightBinding.refresh(){
    root.background = getDrawable(R.drawable.bg_start_route_bt)
}

fun View.getViewRect(): RectF{
    return RectF(
        left.toFloat(),
        top.toFloat(),
        right.toFloat(),
        bottom.toFloat()
    )
}