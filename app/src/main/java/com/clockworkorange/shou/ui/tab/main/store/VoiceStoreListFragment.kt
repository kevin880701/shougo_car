package com.clockworkorange.shou.ui.tab.main.store

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.clockworkorange.repository.domain.VoiceStore
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentVoiceStoreListBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.removeByTag
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.redirection.VoiceActionReceiver
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import com.clockworkorange.shou.ui.tab.main.coupon.ShouCouponDetailFragment
import com.clockworkorange.shou.ui.tab.main.coupon.ShouCouponListFragment
import com.clockworkorange.shou.ui.tab.main.place.ShouPlaceDetailFragment
import com.clockworkorange.shou.ui.tab.main.place.ShouPlaceListFragment
import com.clockworkorange.shou.util.RouteUtil
import com.clockworkorange.shou.util.UnitUtils
import timber.log.Timber


class VoiceStoreListFragment : BaseFragment(), VoiceActionReceiver.Listener {

    companion object {
        const val TAG = "VoiceStoreListFragment"
        const val KEY_VOICE_TAG = "KEY_VOICE_TAG"

        fun newInstance(voiceTag: String) = VoiceStoreListFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_VOICE_TAG, voiceTag)
            }
        }
    }

    private var _binding: FragmentVoiceStoreListBinding? = null
    private val binding get() = _binding!!
    private val mainTabViewModel: MainTabViewModel by viewModels( { requireParentFragment() }, { viewModelFactory } )
    private val viewModel: VoiceStoreViewModel by viewModels{ viewModelFactory }
    private val voiceTag: String by lazy { requireArguments().getString(KEY_VOICE_TAG)!! }

    val adapter = StoreAdapter(::onStoreItemClick)
    private val receiver by lazy { VoiceActionReceiver(this) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceStoreListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        bindViewModel()
        receiver.register(requireContext())
    }

    override fun onResume() {
        super.onResume()
        mainTabViewModel.isVoiceStoreListVisible.value = true
    }

    override fun onStop() {
        super.onStop()
        mainTabViewModel.isVoiceStoreListVisible.value = false
    }

    // used when fragment exist ( not new instance )
    fun setVoiceTag(voiceTag: String) {
        // using arguments prevent restated by android
        arguments = Bundle().apply {
            putString(KEY_VOICE_TAG, voiceTag)
        }
        viewModel.setVoiceTag(voiceTag)
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.ivBg.setBackgroundColor(getColor(R.color.color_on_background))
        binding.ivBack.setImageResource(R.drawable.ic_back)
        binding.tvTitle.setTextColor(getColor(R.color.color_primary))
        binding.tvMsg.setTextColor(getColor(R.color.color_primary))
    }

    fun initView() = binding.run {
        removeOtherChildFrag()
        tvTitle.text = "附近店家"
        rvStore.adapter = adapter
        rvStore.addItemDecoration(VerticalSpaceItemDecoration(8))
        viewModel.setVoiceTag(voiceTag)
    }

    private fun removeOtherChildFrag() {
        parentFragmentManager.removeByTag(ShouCouponListFragment.TAG)
        parentFragmentManager.removeByTag(ShouCouponDetailFragment.TAG)
        parentFragmentManager.removeByTag(ShouPlaceListFragment.TAG)
        parentFragmentManager.removeByTag(ShouPlaceDetailFragment.TAG)
    }

    fun bindViewModel() = binding.run {
        viewModel.voiceStores.observe(viewLifecycleOwner) { stores ->
            adapter.data = stores
            adapter.notifyDataSetChanged()
            rvStore.scrollToPosition(0)
        }

        viewModel.showLoading.observe(viewLifecycleOwner){
            flLoading.isVisible = it
        }

        viewModel.msg.observe(viewLifecycleOwner) {
            tvMsg.text = it
        }

        viewModel.voiceTag.observe(viewLifecycleOwner) {
            tvVoiceTag.text = it
        }
    }

    fun initListener() = binding.run {
        viewClickBack.setOnClickListener {
            parentFragmentManager.removeByTag(TAG)
        }
    }

    private fun onStoreItemClick(store: VoiceStore) {
        openWebPage(store.webUrl)
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

    override fun onSelectItem(index: Int) {

        val currentLocation = mainTabViewModel.currentLocation.value ?: return

        val llm = binding.rvStore.layoutManager as LinearLayoutManager
        val firstPosition = llm.findFirstVisibleItemPosition()
        val list = adapter.data
        val selectIndex = firstPosition + index -1
        if (list.isEmpty()){
            return
        }
        val store = if (selectIndex < list.size) list[selectIndex] else list.last()

        val s = "選第 $index 個 ${store.name} ${store.name}"
        Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
        Timber.d(s)

        RouteUtil.startRoute(
            requireActivity(),
            currentLocation,
            store.lat, store.lng
        )

    }

    override fun onNextPage() {
        binding.rvStore.smoothScrollBy(binding.rvStore.scrollX, UnitUtils.dpToPx(requireContext(),240f))
    }

    override fun onPreviousPage() {
        binding.rvStore.smoothScrollBy(binding.rvStore.scrollX, UnitUtils.dpToPx(requireContext(),-240f))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        receiver.unRegister(requireContext())
        _binding = null
    }
}