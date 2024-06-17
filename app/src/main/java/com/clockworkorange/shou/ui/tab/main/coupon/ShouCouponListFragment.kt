package com.clockworkorange.shou.ui.tab.main.coupon

import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.clockworkorange.shou.ui.tab.main.base.BaseCouponPlaceListFragment
import com.clockworkorange.shou.util.RouteUtil
import timber.log.Timber


class ShouCouponListFragment : BaseCouponPlaceListFragment() {

    companion object {
        const val TAG = "ShouCouponListFragment"
    }


    override fun initView() {
        super.initView()
        binding.tvTitle.text = "優惠店家"
        binding.spCategory.setTitle("全部")
        viewModel.isCouponListVisible.value = true
    }

    override fun bindViewModel() {
        bindCoupon()

        viewModel.isCouponDetailVisible.observe(viewLifecycleOwner){ coupon ->
            val isDetailShowing = coupon != null
            parentFragmentManager.commit {
                if (isDetailShowing){
                    Timber.d("hide coupon list")
                    hide(this@ShouCouponListFragment)
                }else{
                    Timber.d("show coupon list")
                    show(this@ShouCouponListFragment)
                }
            }
        }

        listViewModel.filteredCoupon.observe(viewLifecycleOwner){
            Timber.d("couponAdapter.submitList filteredCoupon")
            couponAdapter.submitList(it)
        }

        viewModel.currentFavCouponIdList.observe(viewLifecycleOwner){
            it ?: return@observe
            couponAdapter.setFavIds(it)
        }

    }


    override fun onSelectItem(index: Int) {

        val currentLocation = viewModel.currentLocation.value ?: return

        val llm = binding.rvCoupon.layoutManager as LinearLayoutManager
        val firstPosition = llm.findFirstVisibleItemPosition()
        val list = couponAdapter.currentList
        val selectIndex = firstPosition + index -1
        if (list.isEmpty()){
            return
        }
        val item = if (selectIndex < list.size) list[selectIndex] else list.last()

        val s = "選第 $index 個 ${item.name} ${item.place.name}"
        Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
        Timber.d(s)

        RouteUtil.startRoute(
            requireActivity(),
            currentLocation,
            item.place.lat, item.place.lng
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isCouponListVisible.value = false
    }
}