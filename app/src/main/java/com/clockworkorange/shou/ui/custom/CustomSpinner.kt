package com.clockworkorange.shou.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.ItemSpinnerBinding
import com.clockworkorange.shou.ext.toPx

class CustomSpinner constructor(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    interface Listener{
        fun onContentTitleClick(item: String)
        fun onItemClick(item: String)
    }

    var listener: Listener? = null

    private val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        binding.flSpinner.setOnClickListener {
            binding.cardContent.isVisible = true
        }

        binding.flSelectContentTitle.setOnClickListener {
            binding.cardContent.isVisible = false
            binding.tvSpinnerTitle.text = binding.tvSelectContentTitle.text
            listener?.onContentTitleClick(binding.tvSelectContentTitle.text.toString())
        }
    }

    fun isContentShowing(): Boolean{
        return binding.cardContent.isVisible
    }

    fun hideContent(){
        binding.cardContent.isVisible = false
    }

    fun setTitle(title: String){
        binding.tvSpinnerTitle.text = title
        binding.tvSelectContentTitle.text = title
    }

    fun setCurrentItem(name: String){
        binding.tvSpinnerTitle.text = name
    }

    private val clickListener = View.OnClickListener {
        val name = it.tag as String
        binding.cardContent.isVisible = false
        binding.tvSpinnerTitle.text = name
        listener?.onItemClick(name)
    }

    fun setSelectContent(items: List<String>){
        binding.llSelectContainer.removeAllViews()
        items.forEachIndexed { index, name ->
            val item = createSelectItem(name)
            item.tag = name
            item.setOnClickListener(clickListener)
            binding.llSelectContainer.addView(item)
            if (index < items.size-1){
                binding.llSelectContainer.addView(createDivider())
            }
        }
    }

    private fun createSelectItem(name: String): View {
        return TextView(context).apply {
            text = name
            tag = name
            setTextColor(Color.parseColor("#3e3a39"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            letterSpacing = 0.1f
            typeface = resources.getFont(R.font.noto_sanstc_medium_500)
            includeFontPadding = false
            gravity = Gravity.CENTER_VERTICAL
            setPadding(21.toPx.toInt(), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 44.toPx.toInt())
        }
    }


    private fun createDivider(): View {
        return View(context).apply {
            background = ColorDrawable(ContextCompat.getColor(context, R.color.color_top_menu_content_divider))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.toPx.toInt()).apply {
                setMargins(18.toPx.toInt(), 0, 18.toPx.toInt(), 0)
            }
        }
    }

}