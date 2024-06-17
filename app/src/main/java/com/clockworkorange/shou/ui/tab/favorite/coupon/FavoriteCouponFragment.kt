package com.clockworkorange.shou.ui.tab.favorite.coupon

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.repository.domain.AccountState
import com.clockworkorange.repository.domain.IShouCoupon
import com.clockworkorange.repository.domain.ShouCouponDetail
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FavoriteCouponFragmentBinding
import com.clockworkorange.shou.ext.*
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.CustomSpinnerContent
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.tab.favorite.FavoriteViewModel
import com.clockworkorange.shou.ui.tab.main.RequireNavigationParkFragment
import com.clockworkorange.shou.ui.tab.main.base.CouponAdapter
import com.clockworkorange.shou.ui.tab.main.base.FavoriteActionListener
import com.clockworkorange.shou.util.QRCodeUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class  FavoriteCouponFragment : BaseFragment(), FavoriteActionListener<IShouCoupon> {

    companion object {
        const val TAG = "FavoriteCouponFragment"
        fun newInstance() = FavoriteCouponFragment()
    }

    private val viewModel: FavoriteViewModel by viewModels ({requireParentFragment()}, {viewModelFactory})

    private val favoriteCouponViewModel: FavoriteCouponViewModel by viewModels { viewModelFactory }

    private var _binding: FavoriteCouponFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { CouponAdapter(this) }

    private var coupon: ShouCouponDetail? = null

    private var qrCodeBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteCouponFragmentBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.rvCoupon.adapter = adapter
        binding.rvCoupon.addItemDecoration(VerticalSpaceItemDecoration(8))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {

        binding.viewTouch.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            if (binding.viewFilterCityContent.isVisible && !binding.viewFilterCityContent.getViewRect().contains(x, y)){
                binding.viewFilterCityContent.isVisible = false
                return@setOnTouchListener  true
            }

            if (binding.viewFilterCategoryContent.isVisible && !binding.viewFilterCategoryContent.getViewRect().contains(x, y)){
                binding.viewFilterCategoryContent.isVisible = false
                return@setOnTouchListener  true
            }

            return@setOnTouchListener false
        }

        binding.ilAddTravel.llAddTravel.setOnClickListener {
            coupon?.let {
                if (it.place.hasParking()){
                    requireNavParking(it)
                }else{
                    viewModel.addTravel(it)
                    refresh(it)
                }
            }
        }

        binding.ilStoreInfo.root.setOnClickListener {
            val storeId = coupon?.place?.placeId ?: return@setOnClickListener
            viewModel.showStoreInfo(storeId)
        }

        binding.flFilterCity.setOnClickListener {
            if (favoriteCouponViewModel.isFavListEmpty()){
                toast("請先新增收藏")
                return@setOnClickListener
            }
            binding.viewFilterCityContent.isVisible = true
        }

        binding.viewFilterCityContent.listener = object : CustomSpinnerContent.Listener{
            override fun onAllClick() {
                favoriteCouponViewModel.setFilterCity("")
                binding.tvFilterCity.text = "全部"
                binding.viewFilterCityContent.isVisible = false
            }

            override fun onItemClick(item: String) {
                favoriteCouponViewModel.setFilterCity(item)
                binding.tvFilterCity.text = item
                binding.viewFilterCityContent.isVisible = false
            }
        }

        binding.flFilterCategory.setOnClickListener {
            if (favoriteCouponViewModel.isFavListEmpty()){
                toast("請先新增收藏")
                return@setOnClickListener
            }
            binding.viewFilterCategoryContent.isVisible = true
        }

        binding.viewFilterCategoryContent.listener = object : CustomSpinnerContent.Listener{
            override fun onAllClick() {
                favoriteCouponViewModel.setFilterCategory("")
                binding.tvFilterCategory.text = "全部"
                binding.viewFilterCategoryContent.isVisible = false
            }

            override fun onItemClick(item: String) {
                favoriteCouponViewModel.setFilterCategory(item)
                binding.tvFilterCategory.text = item
                binding.viewFilterCategoryContent.isVisible = false
            }
        }
    }

    private fun requireNavParking(coupon: ShouCouponDetail) {
        parentFragmentManager.commit {
            val fragment = RequireNavigationParkFragment.newInstance(
                coupon.place.name,
                object : RequireNavigationParkFragment.Listener {
                    override fun onRequireNavigationParkResult(requirePark: Boolean) {
                        viewModel.addTravel(coupon, requirePark)
                        refresh(coupon)
                    }
                })
            add(R.id.fl_fav_full_tab, fragment, RequireNavigationParkFragment.TAG)
        }
    }

    private fun bindViewModel() {
        favoriteCouponViewModel.cityList.observe(viewLifecycleOwner){
            binding.viewFilterCityContent.setSelectContent(it)
        }

        favoriteCouponViewModel.categoryList.observe(viewLifecycleOwner){
            binding.viewFilterCategoryContent.setSelectContent(it)
        }

        favoriteCouponViewModel.filteredCoupon.observe(viewLifecycleOwner){
            adapter.submitList(it)
            adapter.setFavIds(it.map { it.couponId })
            it.firstOrNull()?.let { onItemClick(it) }
            binding.llInfo.isInvisible = it.isEmpty()
        }

        viewModel.eventShowCoupon.observe(viewLifecycleOwner){ placeId ->
            val position = adapter.findCouponPosition(placeId)
            if (position != -1){
                binding.tvFilterCity.text = "全部"
                binding.tvFilterCategory.text = "全部"
                favoriteCouponViewModel.setFilterCity("")
                favoriteCouponViewModel.setFilterCategory("")
                binding.rvCoupon.scrollToPosition(position)
                adapter.clickOnPosition(position)
            }
        }

        viewModel.accountState.observe(viewLifecycleOwner){ state ->
            binding.llInfo.isInvisible = (state == AccountState.NotLogin)
        }
    }

    override fun onFavoriteClick(item: IShouCoupon) {
        viewModel.removeFavorite(item)
    }

    override fun onItemClick(item: IShouCoupon) {
        adapter.selectedId = item.couponId
        coupon = item as ShouCouponDetail
        (item as? ShouCouponDetail)?.let {
            binding.tvAddress.text = it.place.address
            binding.tvDistance.text = " %.1f公里".format(it.place.distance)
            binding.tvDate.text = "%s-%s".format(it.dateStart, it.dateEnd)
            binding.tvDetail.text = it.desc

            qrCodeBitmap?.recycle()
            qrCodeBitmap = null
            qrCodeBitmap = QRCodeUtil.createBitmap(it.qrCodeContent)
            binding.ivQrCode.setImageBitmap(qrCodeBitmap)

            binding.ilAddTravel.setDisable(viewModel.isInCurrentTravel(it))
        }
    }

    private fun refresh(shouCouponDetail: ShouCouponDetail) {
        lifecycleScope.launch {
            delay(300)
            onItemClick(shouCouponDetail)
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.clList.background = getDrawable(R.drawable.bg_round_12_fav_list_background)
        binding.llInfo.background = getDrawable(R.drawable.bg_round_12_fav_info_background)
        binding.flTopInfo.background = getDrawable(R.drawable.bg_round_12_fav_content_background)
        binding.tvDiscount.setTextColor(getColor(R.color.color_favorite_info_title_text))
        binding.viewUnderline.setBackgroundColor(getColor(R.color.color_normal_text))
        binding.tvAddress.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        binding.tvDistance.setTextColor(getColor(R.color.color_favorite_info_sub_text))

        val drawArrow = getDrawable(R.drawable.ic_arrow_right_up)?.apply {
            setBounds(0 ,0 , 20 , 20)
            setTint(getColor(R.color.color_text_drawable))
        }

        binding.tvDistance.setCompoundDrawablesRelative(drawArrow, null, null, null)
        binding.tvDateTitle.setTextColor(getColor(R.color.color_normal_text))
        binding.tvDate.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        binding.tvDetailTitle.setTextColor(getColor(R.color.color_normal_text))
        binding.tvDetail.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        binding.clBottomInfo.background = getDrawable(R.drawable.bg_round_12_fav_content_background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.rvCoupon.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_rv_thumb)
            binding.rvCoupon.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_rv_track)

            binding.svTopInfo.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_sv_thumb)
            binding.svTopInfo.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_sv_track)
        }

        binding.ilAddTravel.refresh()
        binding.ilStoreInfo.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
        System.gc()
        _binding = null
    }
}