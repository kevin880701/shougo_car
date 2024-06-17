package com.clockworkorange.shou.util

import android.content.Context
import android.util.TypedValue

object UnitUtils {
    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}