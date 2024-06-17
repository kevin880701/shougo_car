package com.clockworkorange.shou.util

fun interface AdapterListener<T> {
    fun onItemClick(item: T)
}