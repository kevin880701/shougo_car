package com.clockworkorange.shou.ui.tab.main.place

import androidx.fragment.app.commit
import com.clockworkorange.shou.ui.tab.main.base.BaseCouponPlaceListFragment
import timber.log.Timber

class ShouPlaceListFragment : BaseCouponPlaceListFragment() {

    companion object{
        const val TAG = "ShouPlaceListFragment"
    }

    override fun initView() {
        super.initView()
        binding.tvTitle.text = "私藏景點"
        binding.spCategory.setTitle("全部")
        viewModel.isPlaceListVisible.value = true
    }

    override fun bindViewModel() {
        bindPlace()

        viewModel.isPlaceDetailVisible.observe(viewLifecycleOwner){ place ->
            val isDetailShowing = place != null
            parentFragmentManager.commit {
                if (isDetailShowing){
                    Timber.d("ddd hide place list")
                    hide(this@ShouPlaceListFragment)
                }else{
                    Timber.d("ddd show place list")
                    show(this@ShouPlaceListFragment)
                }
            }
        }

        listViewModel.filteredPlace.observe(viewLifecycleOwner){
            placeAdapter.submitList(it)
        }


        viewModel.currentFavPlaceIdList.observe(viewLifecycleOwner){
            it ?: return@observe
            placeAdapter.setFavIds(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isPlaceListVisible.value = false
    }
}