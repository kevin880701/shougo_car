package com.clockworkorange.shou.ext

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit


fun FragmentManager.removeByTag(tag: String){
    findFragmentByTag(tag)?.let { commit { remove(it) } }
}

fun FragmentManager.showByTag(tag: String){
    findFragmentByTag(tag)?.let { commit { show(it) } }
}

fun FragmentManager.hideByTag(tag: String){
    findFragmentByTag(tag)?.let { commit { hide(it) } }
}