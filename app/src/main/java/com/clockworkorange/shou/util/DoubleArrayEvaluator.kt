package com.clockworkorange.shou.util

import android.animation.TypeEvaluator

class DoubleArrayEvaluator: TypeEvaluator<DoubleArray> {

    override fun evaluate(fraction: Float, startValue: DoubleArray, endValue: DoubleArray): DoubleArray {
        val res = DoubleArray(startValue.size)

        for (i in res.indices){
            val start = startValue[i]
            val end = endValue[i]
            res[i] = start + fraction * (end - start)
        }

        return res
    }
}