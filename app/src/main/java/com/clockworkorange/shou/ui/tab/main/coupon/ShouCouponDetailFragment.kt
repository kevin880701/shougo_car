package com.clockworkorange.shou.ui.tab.main.coupon

import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ext.loadImage
import com.clockworkorange.shou.ext.setDisable
import com.clockworkorange.shou.ui.tab.main.base.BaseCouponPlaceDetailFragment
import com.clockworkorange.shou.util.QRCodeUtil
import com.clockworkorange.shou.util.RouteUtil


class ShouCouponDetailFragment : BaseCouponPlaceDetailFragment() {

    companion object {
        const val TAG = "ShouCouponDetailFragment"
        private const val KEY_COUPON = "KEY_COUPON"


        fun newInstance(coupon: ShouCoupon) = ShouCouponDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_COUPON, coupon)
            }
        }
    }

    private val coupon: ShouCoupon by lazy { requireArguments().getParcelable(KEY_COUPON)!! }

    private val detailViewModel: CouponDetailViewModel by viewModels { viewModelFactory }

    private var qrCodeBitmap: Bitmap? = null

    override fun initView() {
        super.initView()
        viewModel.onCouponDetailShowing(coupon)
        detailViewModel.setCoupon(coupon)
        binding.ilStoreInfo.root.isVisible = true
    }

    override fun initListener() {
        super.initListener()
        binding.ilStoreInfo.root.setOnClickListener {
            viewModel.showPlaceDetail(coupon.place.placeId)
        }

        binding.ilStartRoute.root.setOnClickListener {
            val currentLocation = viewModel.currentLocation.value ?: return@setOnClickListener
            val currentCoupon = detailViewModel.couponDetail.value ?: return@setOnClickListener
            RouteUtil.startRoute(requireActivity(), currentLocation, currentCoupon.place.lat, currentCoupon.place.lng)
        }
    }

    override fun bindViewModel() {
        viewModel.currentTravel.observe(viewLifecycleOwner) {
            it ?: return@observe
            detailViewModel.setCurrentTravel(it)
        }

        detailViewModel.couponDetail.observe(viewLifecycleOwner) { detail ->
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
                tvDistance.text = "%.2f公里".format(detail.place.distance)
            }

        }

        detailViewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            binding.ilAddFav.tvFavorite.text = if (isFav) "已收藏" else "收藏"
            val favBgResId: Int =
                if (isFav) R.drawable.bg_round_66000b else R.drawable.bg_round_c30f23
            binding.ilAddFav.root.setBackgroundResource(favBgResId)
        }

        detailViewModel.isInTravel.observe(viewLifecycleOwner) { isInTravel ->
            binding.ilAddTravel.setDisable(isInTravel)
        }

    }

    override fun onDestroyView() {
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
        super.onDestroyView()
        viewModel.onCouponDetailDisappear()
        System.gc()
    }

    override fun onAddTravelClick() {
        val couponDetail = detailViewModel.couponDetail.value ?: return
        viewModel.addTravel(TravelWayPoint.TravelCoupon(couponDetail))
    }

    override fun onAddFavoriteClick() {
        viewModel.addFavorite(coupon)
    }

}