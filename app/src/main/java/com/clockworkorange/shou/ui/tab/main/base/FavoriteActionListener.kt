package com.clockworkorange.shou.ui.tab.main.base

import com.clockworkorange.shou.util.AdapterListener

interface FavoriteActionListener<R> : AdapterListener<R> {
    fun onFavoriteClick(item: R)
}