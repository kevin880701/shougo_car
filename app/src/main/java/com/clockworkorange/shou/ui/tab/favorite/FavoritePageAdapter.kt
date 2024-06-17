package com.clockworkorange.shou.ui.tab.favorite

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clockworkorange.shou.ui.tab.favorite.coupon.FavoriteCouponFragment
import com.clockworkorange.shou.ui.tab.favorite.placestore.FavoritePlaceFragment
import com.clockworkorange.shou.ui.tab.favorite.placestore.FavoriteStoreFragment
import com.clockworkorange.shou.ui.tab.favorite.travel.FavoriteTravelFragment

class FavoritePageAdapter(fm : FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment{
        return when(position){
            0 -> FavoriteCouponFragment()
            1 -> FavoriteStoreFragment()
            2 -> FavoritePlaceFragment()
            3 -> FavoriteTravelFragment()
            else -> throw IllegalArgumentException("pos($position) not allow ")
        }
    }

}