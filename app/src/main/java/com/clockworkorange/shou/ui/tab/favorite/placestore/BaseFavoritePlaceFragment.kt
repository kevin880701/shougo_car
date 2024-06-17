package com.clockworkorange.shou.ui.tab.favorite.placestore

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.repository.domain.IShouPlace
import com.clockworkorange.repository.domain.ShouPlaceDetail
import com.clockworkorange.repository.model.StoreDesc
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FavoritePlaceFragmentBinding
import com.clockworkorange.shou.ext.*
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.CustomSpinnerContent
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.tab.favorite.FavoriteViewModel
import com.clockworkorange.shou.ui.tab.main.RequireNavigationParkFragment
import com.clockworkorange.shou.ui.tab.main.base.FavoriteActionListener
import com.clockworkorange.shou.ui.tab.main.base.PlaceAdapter
import com.clockworkorange.shou.ui.tab.main.place.PlaceDescDetailAdapter
import com.clockworkorange.shou.util.QRCodeUtil
import com.clockworkorange.shou.util.RouteUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseFavoritePlaceFragment : BaseFragment(), FavoriteActionListener<IShouPlace> {

    private val viewModel: FavoriteViewModel by viewModels ({requireParentFragment()}, {viewModelFactory})
    protected val storePlaceViewModel: FavoriteStorePlaceViewModel by viewModels { viewModelFactory }

    private var _binding: FavoritePlaceFragmentBinding? = null
    protected val binding get() = _binding!!

    protected val adapter by lazy { PlaceAdapter(this) }

    private var place: ShouPlaceDetail? = null

    private var qrCodeBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritePlaceFragmentBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    protected open fun initView() {
        binding.rvPlace.adapter = adapter
        binding.rvPlace.addItemDecoration(VerticalSpaceItemDecoration(8))
    }

    protected open fun onLoadDescDetail(desc: List<StoreDesc>) {}

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
            place?.let {
                if (it.hasParking()){
                    requireNavParking(it)
                }else{
                    viewModel.addTravel(it)
                    refresh(it)
                }
            }
        }

        binding.ilCheckCoupon.root.setOnClickListener {
            viewModel.checkStoreCoupon(place?.placeId ?: return@setOnClickListener)
        }

        binding.flFilterCity.setOnClickListener {
            if (storePlaceViewModel.isFavListEmpty()){
                toast("請先新增收藏")
                return@setOnClickListener
            }
            binding.viewFilterCityContent.isVisible = true
        }

        binding.viewFilterCityContent.listener = object : CustomSpinnerContent.Listener{

            override fun onAllClick() {
                storePlaceViewModel.setCity("")
                binding.tvFilterCity.text = "全部"
                binding.viewFilterCityContent.isVisible = false
            }

            override fun onItemClick(item: String) {
                storePlaceViewModel.setCity(item)
                binding.tvFilterCity.text = item
                binding.viewFilterCityContent.isVisible = false
            }
        }

        binding.flFilterCategory.setOnClickListener {
            if (storePlaceViewModel.isFavListEmpty()){
                toast("請先新增收藏")
                return@setOnClickListener
            }
            binding.viewFilterCategoryContent.isVisible = true
        }

        binding.viewFilterCategoryContent.listener = object : CustomSpinnerContent.Listener{
            override fun onAllClick() {
                storePlaceViewModel.setCategory("")
                binding.tvFilterCategory.text = "全部"
                binding.viewFilterCategoryContent.isVisible = false
            }

            override fun onItemClick(item: String) {
                storePlaceViewModel.setCategory(item)
                binding.tvFilterCategory.text = item
                binding.viewFilterCategoryContent.isVisible = false
            }
        }

        binding.ilStartRoute.root.setOnClickListener {
            val location = viewModel.getLastLocation() ?: return@setOnClickListener
            val destLat = place?.lat ?: return@setOnClickListener
            val destLng = place?.lng ?: return@setOnClickListener
            RouteUtil.startRoute(requireActivity(), location, destLat, destLng)
        }
    }

    private fun requireNavParking(place: ShouPlaceDetail) {
        parentFragmentManager.commit {
            val fragment = RequireNavigationParkFragment.newInstance(
                place.name,
                object : RequireNavigationParkFragment.Listener {
                    override fun onRequireNavigationParkResult(requirePark: Boolean) {
                        viewModel.addTravel(place, requirePark)
                        refresh(place)
                    }
                })
            add(R.id.fl_fav_full_tab, fragment, RequireNavigationParkFragment.TAG)
        }
    }

    protected open fun bindViewModel() {
        storePlaceViewModel.cityList.observe(viewLifecycleOwner){
            binding.viewFilterCityContent.setSelectContent(it)
        }

        storePlaceViewModel.categoryList.observe(viewLifecycleOwner){
            binding.viewFilterCategoryContent.setSelectContent(it)
        }

        storePlaceViewModel.displayPlaceList.observe(viewLifecycleOwner){
            it ?: return@observe
            onPlaceLoaded(it)
        }

        viewModel.eventShowStore.observe(viewLifecycleOwner){ storeId ->
            val position = adapter.findStorePosition(storeId)
            if (position != -1){
                binding.tvFilterCity.text = "全部"
                binding.tvFilterCategory.text = "全部"
                storePlaceViewModel.setCity("")
                storePlaceViewModel.setCategory("")
                binding.rvPlace.scrollToPosition(position)
                adapter.clickOnPosition(position)
            }
        }
    }

    abstract fun onPlaceLoaded(list: List<ShouPlaceDetail>)

    override fun onFavoriteClick(item: IShouPlace) {
        viewModel.removeFavorite(item)
    }

    override fun onItemClick(item: IShouPlace){
        place = item as ShouPlaceDetail
        (item as? ShouPlaceDetail)?.let {
            binding.tvAddress.text = it.address
            binding.tvInfo.text = it.desc
            binding.tvParking.text = if (it.hasParking()) "有特約停車場。" else "無特約停車場。"
            binding.tvDistance.text = " %.1f公里".format(it.distance)
            binding.tvStar.text = " %.1f".format(it.star)

            qrCodeBitmap?.recycle()
            qrCodeBitmap = null
            qrCodeBitmap = QRCodeUtil.createBitmap(it.qrCodeContent)
            binding.ivQrCode.setImageBitmap(qrCodeBitmap)

            binding.ilAddTravel.setDisable(viewModel.isInCurrentTravel(it))

            if (it.detailDesc.isNotEmpty()){
                onLoadDescDetail(it.detailDesc)
            }else{
                binding.tvDescDetail.isVisible = false
                binding.rvDescDetail.isVisible = false
            }
        }
        adapter.selectedId = item.placeId
    }

    private fun refresh(item: IShouPlace){
        lifecycleScope.launch {
            delay(200)
            onItemClick(item)
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.clList.background = getDrawable(R.drawable.bg_round_12_fav_list_background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.rvPlace.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_rv_thumb)
            binding.rvPlace.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_rv_track)
            binding.svTopInfo.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_sv_thumb)
            binding.svTopInfo.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_sv_track)
        }

        binding.llInfo.background = getDrawable(R.drawable.bg_round_12_fav_info_background)
        binding.flTopInfo.background = getDrawable(R.drawable.bg_round_12_fav_content_background)

        binding.tvStoreInfo.setTextColor(getColor(R.color.color_normal_text))
        binding.viewUnderline.setBackgroundColor(getColor(R.color.color_normal_text))
        binding.tvAddress.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        binding.tvDistance.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        val drawArrow = getDrawable(R.drawable.ic_arrow_right_up)?.apply {
            setBounds(0 ,0 , 20 , 20)
            setTint(getColor(R.color.color_text_drawable))
        }
        binding.tvDistance.setCompoundDrawablesRelative(drawArrow, null, null, null)

        binding.tvInfoTitle.setTextColor(getColor(R.color.color_normal_text))
        binding.tvStar.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        val drawStar = getDrawable(R.drawable.ic_star)?.apply {
            setBounds(0 ,0 , 20 , 20)
            setTint(getColor(R.color.color_text_drawable))
        }
        binding.tvStar.setCompoundDrawablesRelative(drawStar, null, null, null)

        binding.tvInfo.setTextColor(getColor(R.color.color_favorite_info_sub_text))
        binding.tvParkingTitle.setTextColor(getColor(R.color.color_normal_text))
        binding.tvParking.setTextColor(getColor(R.color.color_favorite_info_sub_text))

        binding.clBottomInfo.background = getDrawable(R.drawable.bg_round_12_fav_content_background)

        binding.ilAddTravel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
        _binding = null
        System.gc()
    }
}