package com.clockworkorange.shou.ui.tab.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FavoriteFragmentBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getColorStateList
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ui.MainViewModel
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.favorite.travel.RequireDeleteTravelFragment
import com.clockworkorange.shou.ui.tab.main.RequireLoginFragment

class FavoriteFragment : BaseFragment() {

    companion object {
        const val TAG = "FavoriteFragment"
        fun newInstance() = FavoriteFragment()
    }

    enum class FavoriteTab {
        Coupon, Store, Place, Travel
    }

    private val viewModel: FavoriteViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels { viewModelFactory }

    private var _binding: FavoriteFragmentBinding? = null
    private val binding get() = _binding!!

    private val pageAdapter by lazy { FavoritePageAdapter(childFragmentManager, lifecycle) }

    private var currentTab: FavoriteTab = FavoriteTab.Coupon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteFragmentBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        clickTab(FavoriteTab.Coupon)
    }

    private fun initView() {
        with(binding){
            vpContainer.offscreenPageLimit = 2
            vpContainer.adapter = pageAdapter
            vpContainer.isUserInputEnabled = false
            vpContainer.setCurrentItem(0, false)
        }
    }

    private fun initListener() {
        with(binding){
            tvCoupon.setOnClickListener { clickTab(FavoriteTab.Coupon) }
            tvStore.setOnClickListener { clickTab(FavoriteTab.Store) }
            tvAttraction.setOnClickListener { clickTab(FavoriteTab.Place) }
            tvTravel.setOnClickListener { clickTab(FavoriteTab.Travel) }
        }
    }

    private fun bindViewModel() {
        viewModel.eventToast.observe(viewLifecycleOwner){
            toast(it)
        }

        viewModel.eventShowRequireDeleteTravel.observe(viewLifecycleOwner){ travel ->

            val fragment = RequireDeleteTravelFragment.newInstance { needDelete ->
                if (needDelete) viewModel.removeFavorite(travel)
            }

            childFragmentManager.commit {
                add(R.id.fl_fav_full_tab, fragment, RequireDeleteTravelFragment.TAG)
                addToBackStack("")
            }

        }

        mainViewModel.eventOpenFavorite.observe(viewLifecycleOwner){ favTarget ->
            if (currentTab != favTarget) clickTab(favTarget)
        }

        viewModel.eventChangeFavoriteTab.observe(viewLifecycleOwner){
            clickTab(it)
        }

        viewModel.eventShowRequireLogin.observe(viewLifecycleOwner){
            val fragment = RequireLoginFragment.newInstance(false, object : RequireLoginFragment.Listener{
                override fun noLogin() {}
            })
            childFragmentManager.commit {
                add(R.id.fl_fav_full_tab, fragment, RequireLoginFragment.TAG)
                addToBackStack("")
            }
        }
    }

    private fun clickTab(tab: FavoriteTab){

        currentTab = tab
        binding.tvCoupon.isChecked = false
        binding.tvStore.isChecked = false
        binding.tvAttraction.isChecked = false
        binding.tvTravel.isChecked = false

        when(tab){
            FavoriteTab.Coupon -> {
                binding.tvCoupon.isChecked = true
                binding.vpContainer.setCurrentItem(0, false)
            }
            FavoriteTab.Store -> {
                binding.tvStore.isChecked = true
                binding.vpContainer.setCurrentItem(1, false)
            }
            FavoriteTab.Place -> {
                binding.tvAttraction.isChecked = true
                binding.vpContainer.setCurrentItem(2, false)
            }
            FavoriteTab.Travel -> {
                binding.tvTravel.isChecked = true
                binding.vpContainer.setCurrentItem(3, false)
            }
        }

        viewModel.refreshFavorite()
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.root.setBackgroundColor(getColor(R.color.color_background))

        binding.tvCoupon.background = getDrawable(R.drawable.bg_favorite_tab)
        binding.tvCoupon.setTextColor(getColorStateList(R.color.color_state_favorite_tab_text))

        binding.tvStore.background = getDrawable(R.drawable.bg_favorite_tab)
        binding.tvStore.setTextColor(getColorStateList(R.color.color_state_favorite_tab_text))

        binding.tvAttraction.background = getDrawable(R.drawable.bg_favorite_tab)
        binding.tvAttraction.setTextColor(getColorStateList(R.color.color_state_favorite_tab_text))

        binding.tvTravel.background = getDrawable(R.drawable.bg_favorite_tab)
        binding.tvTravel.setTextColor(getColorStateList(R.color.color_state_favorite_tab_text))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}