package com.clockworkorange.shou.ui.tab.favorite.travel

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.repository.domain.ShouTravelDetail
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FavoriteTravelFragmentBinding
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ext.refresh
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.tab.favorite.FavoriteViewModel
import com.clockworkorange.shou.ui.tab.main.base.FavoriteActionListener
import com.clockworkorange.shou.util.RouteUtil

class FavoriteTravelFragment : BaseFragment(), FavoriteActionListener<ShouTravel> {

    companion object {
        const val TAG = "FavoriteTravelFragment"
        fun newInstance() = FavoriteTravelFragment()
    }

    private var travelDetail: ShouTravelDetail? = null
    private var _binding: FavoriteTravelFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels ({requireParentFragment()}, {viewModelFactory})

    private val adapter: TravelListAdapter by lazy { TravelListAdapter(this) }
    private val waypointAdapter: SimpleWayPointAdapter by lazy { SimpleWayPointAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteTravelFragmentBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.rvTravel.adapter = adapter
        binding.rvTravel.addItemDecoration(VerticalSpaceItemDecoration(8))

        binding.rvWaypoint.adapter = waypointAdapter
        binding.rvWaypoint.addItemDecoration(VerticalSpaceItemDecoration(8))
    }

    private fun initListener() {

        binding.ilStartRoute.root.setOnClickListener {

            travelDetail?.let {
                val last = viewModel.getLastLocation()
                if (last != null){
                    RouteUtil.startRoute(requireContext(), last, it.waypoints)
                }else{
                    toast("無法取得GPS位置")
                }
            }
        }
    }

    private fun bindViewModel() {

        viewModel.eventToast.observe(viewLifecycleOwner){toast(it)}

        viewModel.favoriteTravelList.observe(viewLifecycleOwner){
            adapter.submitList(it)
            binding.clInfo.isInvisible = it.isEmpty()
            it.firstOrNull()?.let { onItemClick(it) }
        }

        viewModel.favoriteTravelDetail.observe(viewLifecycleOwner){
            this.travelDetail = it
            waypointAdapter.submitList(it.waypoints)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFavoriteClick(item: ShouTravel) {
        viewModel.onRemoveFavoriteTravelClick(item)
    }

    override fun onItemClick(item: ShouTravel) {
        adapter.selectedId = item.travelId
        viewModel.fetchTravelDetail(item)
    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.clList.background = getDrawable(R.drawable.bg_round_12_fav_list_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.rvTravel.verticalScrollbarThumbDrawable = getDrawable(R.drawable.bg_rv_thumb)
            binding.rvTravel.verticalScrollbarTrackDrawable = getDrawable(R.drawable.bg_rv_track)
        }

        binding.clInfo.background = getDrawable(R.drawable.bg_round_12_fav_list_background)
        binding.ilStartRoute.refresh()
    }
}
