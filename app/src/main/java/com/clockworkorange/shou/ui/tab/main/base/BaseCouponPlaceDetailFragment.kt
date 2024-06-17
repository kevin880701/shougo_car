package com.clockworkorange.shou.ui.tab.main.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentCouponPlaceDetailBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.refresh
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel

abstract class BaseCouponPlaceDetailFragment : BaseFragment() {

    private var _binding: FragmentCouponPlaceDetailBinding? = null
    protected val binding get() = _binding!!

    protected val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCouponPlaceDetailBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    protected open fun initView(){}

    protected open fun initListener() {
        binding.viewClickBack.setOnClickListener {
            parentFragmentManager.commit {
                remove(this@BaseCouponPlaceDetailFragment)
            }
        }

        binding.ilAddTravel.llAddTravel.setOnClickListener {
            onAddTravelClick()
        }

        binding.ilAddFav.root.setOnClickListener {
            onAddFavoriteClick()
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.ivBg.setBackgroundColor(getColor(R.color.color_on_background))
        binding.ivBack.setImageResource(R.drawable.ic_back)
        binding.tvTitle.setTextColor(getColor(R.color.color_primary))
        binding.tvDesc.setTextColor(getColor(R.color.color_normal_text))

        binding.ilAddTravel.refresh()
        binding.ilStoreInfo.refresh()

        binding.tvScan.setTextColor(getColor(R.color.color_normal_text))

    }

    protected open fun bindViewModel() {}

    abstract fun onAddTravelClick()
    abstract fun onAddFavoriteClick()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}