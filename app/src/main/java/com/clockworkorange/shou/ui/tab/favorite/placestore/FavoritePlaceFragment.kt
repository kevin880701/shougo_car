package com.clockworkorange.shou.ui.tab.favorite.placestore

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.clockworkorange.repository.domain.ShouPlaceDetail

class FavoritePlaceFragment : BaseFavoritePlaceFragment() {

    companion object {
        const val TAG = "FavoritePlaceFragment"
        fun newInstance() = FavoritePlaceFragment()
    }

    override fun initView() {
        super.initView()
        binding.tvStoreInfo.text = "景點資訊"
        binding.ilCheckCoupon.root.isVisible = false
    }

    override fun bindViewModel() {
        super.bindViewModel()
        storePlaceViewModel.setPreFilterCategory{
            it.categoryId == 61
        }
    }

    override fun onPlaceLoaded(list: List<ShouPlaceDetail>) {
        adapter.submitList(list)
        adapter.setFavIds(list.map { it.placeId })
        list.firstOrNull()?.let { onItemClick(it) }
        binding.llInfo.isInvisible = list.isEmpty()
    }

}