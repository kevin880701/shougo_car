package com.clockworkorange.shou.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clockworkorange.shou.ui.tab.account.AccountFragment
import com.clockworkorange.shou.ui.tab.favorite.FavoriteFragment
import com.clockworkorange.shou.ui.tab.main.MainTabFragment
import com.clockworkorange.shou.ui.tab.setting.SettingFragment


class MainTabAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> AccountFragment()
            1 -> MainTabFragment()
            2 -> FavoriteFragment()
            3 -> SettingFragment()
            else -> throw IllegalArgumentException("position($position) not allow")
        }
    }

}