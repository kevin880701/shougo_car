package com.clockworkorange.shou.ext

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment


fun Fragment.getColor(@ColorRes id: Int) = requireContext().getColor(id)

fun Fragment.getDrawable(@DrawableRes id: Int): Drawable = DrawableCompat.wrap(requireContext().getDrawable(id)!!)

fun Fragment.getColorStateList(@ColorRes id: Int) = requireContext().getColorStateList(id)

