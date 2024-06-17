package com.clockworkorange.shou.ui.tab.main.traveltoadd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.databinding.FragmentSimpleListBinding
import com.clockworkorange.shou.ext.addVerticalSpace
import com.clockworkorange.shou.ui.base.BaseFragment

class SelectTravelToAddFragment : BaseFragment(), SelectTravelAdapter.Listener {

    companion object {
        const val TAG = "SelectTravelToAddFragment"
        private const val KEY_WAYPOINT = "KEY_WAYPOINT"
        fun newInstance(wayPoint: TravelWayPoint) = SelectTravelToAddFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_WAYPOINT, wayPoint)
                }
            }
    }

    private var _binding: FragmentSimpleListBinding? = null
    private val binding  get() = _binding!!

    private val viewModel: SelectTravelToAddViewModel by viewModels { viewModelFactory }

    private val adapter by lazy { SelectTravelAdapter(this) }

    private val waypoint: TravelWayPoint by lazy { requireArguments().getParcelable<TravelWayPoint>(KEY_WAYPOINT)!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleListBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.tvTitle.text = "將此地點加到哪個旅程中呢？"
        binding.rvItem.addVerticalSpace(5)
        binding.rvItem.adapter = adapter
    }

    private fun initListener() {
        binding.viewClose.setOnClickListener { removeSelf() }
    }

    private fun bindViewModel() {
        viewModel.eventToast.observe(viewLifecycleOwner){ toast(it) }

        viewModel.showLoading.observe(viewLifecycleOwner){
            binding.pbLoading.isVisible = it
        }

        viewModel.eventExit.observe(viewLifecycleOwner){ removeSelf() }

        viewModel.travelList.observe(viewLifecycleOwner){
            adapter.data.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addToCurrentTravel() {
        viewModel.addToCurrentTravel(waypoint)
    }

    override fun onItemClick(travel: ShouTravel) {
        viewModel.addToTravel(waypoint, travel)
    }
}