package com.clockworkorange.shou.ui.tab.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.clockworkorange.shou.BuildConfig
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentSettingBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ui.base.BaseFragment

class SettingFragment : BaseFragment() {

    companion object {
        const val TAG = "SettingFragment"
    }

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.tvVersion.text = "目前版本 Version ${BuildConfig.VERSION_NAME}"
    }

    private fun initListener() {

        binding.root.setOnClickListener { }

        binding.flNotification.setOnClickListener {
            childFragmentManager.commit {
                add(R.id.fl_container, SettingNotificationFragment(), SettingNotificationFragment.TAG)
                addToBackStack("")
            }
        }

        binding.flPrivacy.setOnClickListener {
            childFragmentManager.commit {
                add(R.id.fl_container, ReviewPrivacyFragment(), ReviewPrivacyFragment.TAG)
                addToBackStack("")
            }
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.root.setBackgroundColor(getColor(R.color.color_background))
        binding.tvSystemSetting.setTextColor(getColor(R.color.color_secondary))

        binding.flNotification.background = getDrawable(R.drawable.bg_setting_item)
        binding.tvNotification.setTextColor(getColor(R.color.color_normal_text))
        binding.ivNotificationDot.setImageDrawable(getDrawable(R.drawable.ic_three_dot))

        binding.flPrivacy.background = getDrawable(R.drawable.bg_setting_item)
        binding.tvPrivacy.setTextColor(getColor(R.color.color_normal_text))
        binding.ivPrivacyDot.setImageDrawable(getDrawable(R.drawable.ic_three_dot))
    }
}