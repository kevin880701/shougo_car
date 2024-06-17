package com.clockworkorange.shou.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.clockworkorange.shou.databinding.WidgetOrderButtonBinding

class OrderButton constructor(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    interface Listener {
        fun onOrderUp()
        fun onOrderDown()
    }

    var listener: Listener? = null

    private var binding: WidgetOrderButtonBinding =
        WidgetOrderButtonBinding.inflate(LayoutInflater.from(context), this, true)

    private val colorDisableBackground = Color.parseColor("#c4c4c4")
    private val colorDisableTriangle = Color.parseColor("#9d9d9d")

    private val colorEnableBackground = Color.parseColor("#4D87B7")
    private val colorEnableTriangle = Color.parseColor("#FFC80B")

    init {
        enableAllButton()

        with(binding){
            ivUp.setOnClickListener {
                listener?.onOrderUp()
            }

            ivDown.setOnClickListener {
                listener?.onOrderDown()
            }
        }
    }

    fun disableUpButton(){
        with(binding){
            viewDivider.isVisible = false

            ivUp.setColorFilter(colorDisableBackground)
            ivUpTriangle.setColorFilter(colorDisableTriangle)

            ivDown.setColorFilter(colorEnableBackground)
            ivDownTriangle.setColorFilter(colorEnableTriangle)
        }
    }

    fun disableDownButton(){
        with(binding){
            viewDivider.isVisible = false

            ivUp.setColorFilter(colorEnableBackground)
            ivUpTriangle.setColorFilter(colorEnableTriangle)

            ivDown.setColorFilter(colorDisableBackground)
            ivDownTriangle.setColorFilter(colorDisableTriangle)
        }
    }

    fun enableAllButton(){
        with(binding){
            viewDivider.isVisible = true

            ivUp.setColorFilter(colorEnableBackground)
            ivUpTriangle.setColorFilter(colorEnableTriangle)

            ivDown.setColorFilter(colorEnableBackground)
            ivDownTriangle.setColorFilter(colorEnableTriangle)
        }
    }

}