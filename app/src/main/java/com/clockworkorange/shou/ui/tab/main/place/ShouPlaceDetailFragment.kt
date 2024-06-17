package com.clockworkorange.shou.ui.tab.main.place

import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ext.loadImage
import com.clockworkorange.shou.ext.setDisable
import com.clockworkorange.shou.ui.tab.main.base.BaseCouponPlaceDetailFragment
import com.clockworkorange.shou.util.QRCodeUtil
import com.clockworkorange.shou.util.RouteUtil

class ShouPlaceDetailFragment : BaseCouponPlaceDetailFragment() {

    companion object {
        const val TAG = "ShouPlaceDetailFragment"
        private const val KEY_PLACE = "KEY_PLACE"

        fun newInstance(place: ShouPlace) = ShouPlaceDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_PLACE, place)

            }
        }
    }

    private val place: ShouPlace by lazy { requireArguments().getParcelable(KEY_PLACE)!!}

    private val placeDetailViewModel: PlaceDetailViewModel by viewModels { viewModelFactory }

    private var qrCodeBitmap: Bitmap? = null

    private val descDetailAdapter: PlaceDescDetailAdapter by lazy { PlaceDescDetailAdapter() }

    override fun initView() {
        viewModel.onPlaceDetailShowing(place)
        placeDetailViewModel.setPlace(place)
        binding.ilCheckCoupon.root.isVisible = true

        binding.tvParkingTitle.isVisible = true
        binding.tvParking.isVisible = true
        binding.rvDescDetail.adapter = descDetailAdapter
        binding.rvDescDetail.isNestedScrollingEnabled = false
        binding.ilStartRoute.root.isVisible = true
        binding.ilStartRoute.tvStartRoute.text = "立即導航"
    }

    override fun initListener() {
        super.initListener()
        binding.ilCheckCoupon.root.setOnClickListener {
            viewModel.showCouponDetail(place.placeId)
        }

        binding.ilStartRoute.root.setOnClickListener {
            val currentLocation = viewModel.currentLocation.value ?: return@setOnClickListener
            val currentPlace = placeDetailViewModel.placeDetail.value ?: return@setOnClickListener
            RouteUtil.startRoute(requireActivity(), currentLocation, currentPlace.lat, currentPlace.lng)
        }
    }

    override fun bindViewModel() {
        viewModel.eventToast.observe(viewLifecycleOwner){toast(it)}
        viewModel.currentTravel.observe(viewLifecycleOwner){
            it ?: return@observe
            placeDetailViewModel.setCurrentTravel(it)
        }

        placeDetailViewModel.placeDetail.observe(viewLifecycleOwner) { detail ->
            with(binding) {
                tvTitle.text = detail.name
                tvDesc.text = detail.desc
                ivCoupon.loadImage(detail.image)
                val qrCodeContent = detail.qrCodeContent
                if (qrCodeContent.isNotEmpty()) {
                    ivQrCode.isInvisible = false

                    qrCodeBitmap?.recycle()
                    qrCodeBitmap = null
                    qrCodeBitmap = QRCodeUtil.createBitmap(qrCodeContent)

                    ivQrCode.setImageBitmap(qrCodeBitmap)
                } else {
                    ivQrCode.isInvisible = true
                }
                tvDistance.text = "%.2f公里".format(detail.distance)

                tvParking.text = if (detail.hasParking()) "有特約停車場" else "無特約停車場"

                if (detail.detailDesc.isNotEmpty()){
                    binding.tvDetailTitle.isVisible = true
                    binding.rvDescDetail.isVisible = true
                    descDetailAdapter.submitList(detail.detailDesc)
                }
            }

        }

        placeDetailViewModel.isFavorite.observe(viewLifecycleOwner){ isFav ->
            binding.ilAddFav.tvFavorite.text = if (isFav) "已收藏" else "收藏"
            val favBgResId: Int = if (isFav) R.drawable.bg_round_66000b else R.drawable.bg_round_c30f23
            binding.ilAddFav.root.setBackgroundResource(favBgResId)
        }

        placeDetailViewModel.isInTravel.observe(viewLifecycleOwner){ isInTravel ->
            binding.ilAddTravel.setDisable(isInTravel)
        }

    }

    override fun onDestroyView() {
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
        super.onDestroyView()
        viewModel.onPlaceDetailDisappear()
        System.gc()
    }


    override fun onAddTravelClick() {
        val placeDetail = placeDetailViewModel.placeDetail.value ?: return
        viewModel.addTravel(TravelWayPoint.TravelPlace(placeDetail))
    }

    override fun onAddFavoriteClick() {
        viewModel.addFavorite(place)
    }
}