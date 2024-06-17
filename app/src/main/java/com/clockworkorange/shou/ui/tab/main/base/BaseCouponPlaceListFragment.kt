package com.clockworkorange.shou.ui.tab.main.base

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.clockworkorange.repository.domain.IShouCoupon
import com.clockworkorange.repository.domain.IShouPlace
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentCouponPlaceListBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ext.getViewRect
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.CustomSpinner
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.redirection.VoiceActionReceiver
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel

abstract class BaseCouponPlaceListFragment : BaseFragment(),  VoiceActionReceiver.Listener {

    private var _binding: FragmentCouponPlaceListBinding? = null
    protected val binding get() = _binding!!

    protected val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })
    protected val listViewModel: CouponPlaceListViewModel by viewModels { viewModelFactory }

    private val couponAdapterListener = object :FavoriteActionListener<IShouCoupon>{
        override fun onFavoriteClick(item: IShouCoupon) {
            if (item.isCouponIdValid()) viewModel.addFavorite(item)
        }

        override fun onItemClick(item: IShouCoupon) {
            if (item.isCouponIdValid()) viewModel.clickCoupon(item as ShouCoupon)
            else openWebPage((item as ShouCoupon).webUrl)
        }
    }

    private fun openWebPage(webUrl: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(webUrl)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast(getString(R.string.no_browser))
        }
    }

    private val placeAdapterListener = object :FavoriteActionListener<IShouPlace>{
        override fun onFavoriteClick(item: IShouPlace) {
            viewModel.addFavorite(item)
        }

        override fun onItemClick(item: IShouPlace) {
            viewModel.clickPlace(item as ShouPlace)
        }
    }

    protected val couponAdapter by lazy { CouponAdapter(couponAdapterListener) }
    protected val placeAdapter by lazy { PlaceAdapter(placeAdapterListener) }

    private val concatAdapter by lazy { ConcatAdapter().apply {
        addAdapter(placeAdapter)
        addAdapter(couponAdapter)
    } }

    private val receiver by lazy { VoiceActionReceiver(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCouponPlaceListBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        receiver.register(requireContext())
    }

    override fun onSelectItem(index: Int) {
        //from voice input
    }

    override fun onNextPage() {
        //from voice input
    }

    override fun onPreviousPage() {
        //from voice input
    }

    protected open fun initView() {
        binding.rvCoupon.adapter = concatAdapter
        binding.rvCoupon.addItemDecoration(VerticalSpaceItemDecoration(8))
    }

    @SuppressLint("ClickableViewAccessibility")
    protected open fun initListener() {

        binding.clRoot.setOnClickListener {
            //ignore
        }

        binding.viewTouch.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            if (binding.spCategory.isContentShowing() && !binding.spCategory.getViewRect().contains(x, y)){
                binding.spCategory.hideContent()
                return@setOnTouchListener  true
            }

            return@setOnTouchListener false
        }

        binding.viewClickBack.setOnClickListener {
            parentFragmentManager.commit {
                remove(this@BaseCouponPlaceListFragment)
            }
        }

        binding.spCategory.listener = object : CustomSpinner.Listener{
            override fun onContentTitleClick(item: String) {
                //select all
                listViewModel.setNoFilter()
            }

            override fun onItemClick(item: String) {
                //select category ex:餐廳...
                listViewModel.setFilter(item)
            }
        }
    }

    protected fun bindCoupon(){
        viewModel.displayCoupon.observe(viewLifecycleOwner){
            listViewModel.setData(it)
            binding.spCategory.setSelectContent(it.map { it.place.categoryName }.toHashSet().toList())
        }
    }

    protected fun bindPlace(){
        viewModel.displayPlace.observe(viewLifecycleOwner){
            listViewModel.setData(it)
            binding.spCategory.setSelectContent(it.map { it.categoryName }.toHashSet().toList())
        }
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.ivBg.setBackgroundColor(getColor(R.color.color_on_background))
        binding.ivBack.setImageResource(R.drawable.ic_back)
        binding.tvTitle.setTextColor(getColor(R.color.color_primary))
        binding.spCategory.background = getDrawable(R.drawable.bg_category)
    }

    abstract fun bindViewModel()

    override fun onDestroyView() {
        super.onDestroyView()
        receiver.unRegister(requireContext())
        _binding = null
    }
}