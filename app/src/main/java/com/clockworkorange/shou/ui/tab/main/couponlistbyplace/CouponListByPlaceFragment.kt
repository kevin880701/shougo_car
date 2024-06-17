package com.clockworkorange.shou.ui.tab.main.couponlistbyplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.shou.databinding.FragmentSimpleListBinding
import com.clockworkorange.shou.ext.addVerticalSpace
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import com.clockworkorange.shou.util.AdapterListener

class CouponListByPlaceFragment : BaseFragment(), AdapterListener<ShouCoupon> {

    companion object{
        const val TAG = "CouponListByPlaceFragment"
        private const val KEY_COUPON_LIST = "KEY_COUPON_LIST"
        fun newInstance(data: List<ShouCoupon>) = CouponListByPlaceFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(KEY_COUPON_LIST, ArrayList(data))
            }
        }
    }

    private var _binding: FragmentSimpleListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })

    private val data: ArrayList<ShouCoupon> by lazy { arguments?.getParcelableArrayList(KEY_COUPON_LIST) ?: ArrayList() }

    private val adapter by lazy { CouponTitleAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleListBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        binding.tvTitle.text = "${data[0].place.name} 優惠券"
        binding.rvItem.adapter = adapter
        binding.rvItem.addVerticalSpace(5)
        adapter.data.addAll(data)
    }

    private fun initListener() {
        binding.viewClose.setOnClickListener {
            removeSelf()
        }
    }

    override fun onItemClick(item: ShouCoupon) {
        viewModel.clickCoupon(item)
        removeSelf()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}