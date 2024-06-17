package com.clockworkorange.shou.ui.tab.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentSettingNotificationBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ui.base.BaseFragment


class SettingNotificationFragment : BaseFragment() {

    companion object {

        const val TAG = "SettingNotificationFragment"

        fun newInstance() = SettingNotificationFragment()
    }

    private var _binding: FragmentSettingNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingNotificationBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {

        binding.root.setOnClickListener { }

        binding.swNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            val test = if (isChecked) "啟用推播" else "停用推播"
            toast(test)
        }

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.root.setBackgroundColor(getColor(R.color.color_background))
        binding.clNotification.background = getDrawable(R.drawable.bg_setting_item)
        binding.ivBack.setImageResource(R.drawable.ic_back)
        binding.tvNotificationSetting.setTextColor(getColor(R.color.color_primary))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}