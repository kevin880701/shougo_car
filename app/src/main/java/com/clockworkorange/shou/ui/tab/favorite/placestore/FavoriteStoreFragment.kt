package com.clockworkorange.shou.ui.tab.favorite.placestore

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.clockworkorange.repository.domain.ShouPlaceDetail
import com.clockworkorange.repository.model.StoreDesc
import com.clockworkorange.shou.ui.tab.main.place.PlaceDescDetailAdapter

class FavoriteStoreFragment : BaseFavoritePlaceFragment() {

    companion object {
        const val TAG = "FavoriteStoreFragment"
        fun newInstance() = FavoriteStoreFragment()
    }

    override fun initView() {
        super.initView()
        binding.tvStoreInfo.text = "商店資訊"
        binding.ilStartRoute.tvStartRoute.text = "立即導航"
        binding.ilStartRoute.root.isVisible = true
    }

    override fun bindViewModel() {
        super.bindViewModel()
        storePlaceViewModel.setPreFilterCategory{
            it.categoryId != 61
        }
    }

    override fun onPlaceLoaded(list: List<ShouPlaceDetail>) {
        adapter.submitList(list)
        adapter.setFavIds(list.map { it.placeId })
        list.firstOrNull()?.let { onItemClick(it) }
        binding.llInfo.isInvisible = list.isEmpty()
    }

    override fun onLoadDescDetail(desc: List<StoreDesc>) {
        val adapter = PlaceDescDetailAdapter()
        adapter.submitList(desc)
        binding.rvDescDetail.adapter = adapter
        binding.tvDescDetail.isVisible = true
        binding.rvDescDetail.isVisible = true
    }
}