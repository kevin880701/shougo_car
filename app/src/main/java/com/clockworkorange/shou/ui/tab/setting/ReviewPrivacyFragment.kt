package com.clockworkorange.shou.ui.tab.setting

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentReviewPrivacyBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ui.base.BaseFragment


class ReviewPrivacyFragment : BaseFragment() {

    companion object {
        const val TAG = "ReviewPrivacyFragment"
    }

    private var _binding: FragmentReviewPrivacyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewPrivacyBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.root.setOnClickListener {  }

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.root.setBackgroundColor(getColor(R.color.color_background))
        binding.clPrivacy.background = getDrawable(R.drawable.bg_setting_item)
        binding.ivBack.setImageResource(R.drawable.ic_back)
        binding.tvPrivacyTitle.setTextColor(getColor(R.color.color_primary))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.svPrivacy.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_sv_track)
            binding.svPrivacy.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_sv_thumb)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}