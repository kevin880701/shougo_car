package com.clockworkorange.shou.ui.tab.main.travel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.repository.remote.http.ApiException
import com.clockworkorange.shou.databinding.FragmentMyTravelBinding
import com.clockworkorange.shou.ext.hideKeyboard
import com.clockworkorange.shou.ext.showKeyboard
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MyTravelFragment : BaseFragment(), MyTravelAdapter.Listener {

    companion object{
        const val TAG = "MyTravelFragment"
    }

    private var _binding: FragmentMyTravelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })

    private val adapter = MyTravelAdapter(this)

    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTravelBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.rvTravelPlace.adapter = adapter
        binding.rvTravelPlace.addItemDecoration(VerticalSpaceItemDecoration(8))

        val dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val callback = object : ItemTouchHelper.SimpleCallback(dragDirs, 0){
            var fromPosition: Int = 0
            var toPosition: Int = 0
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                fromPosition = viewHolder.adapterPosition
                toPosition = target.adapterPosition
                viewModel.swapTravelOrder(fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                return false
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun isLongPressDragEnabled(): Boolean = false
        }

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvTravelPlace)

        viewModel.isMyTravelVisible.value = true
    }

    private fun initListener() {

        binding.root.setOnClickListener {
            removeSelf()
        }

        binding.ivBack.setOnClickListener {
            removeSelf()
        }

        binding.ilTravelState.root.setOnClickListener {
            removeSelf()
        }

        binding.ilStartRoute.root.setOnClickListener {
            if (viewModel.isTravelEmpty()){
                toast("尚未設定旅程，請先新增旅程")
            }else{
                removeSelf()
                viewModel.startRouteTravel()
            }
        }

        binding.flAddFavoriteTravel.setOnClickListener {
            if (viewModel.isTravelEmpty()){
                toast("尚未設定旅程，請先新增旅程")
            }else{
                binding.tvNote.isVisible = false
                binding.clTravel.isVisible = false
                binding.clNamingTravel.isVisible = true
                binding.etTravelName.requestFocus()
                requireContext().showKeyboard()
            }
        }

        binding.btConfirmName.setOnClickListener {
            val travelName = binding.etTravelName.editableText.toString()
            if (travelName.isEmpty() or travelName.isBlank()){
                toast("旅程名稱不能為空")
                return@setOnClickListener
            }
            lifecycleScope.launch(genericExceptionHandler){
                try {
                    val id = viewModel.saveTravel(travelName)
                    if (id != 0){
                        viewModel.clearCurrentTravel()
                        removeSelf()
                        toast("新增成功")
                    }
                }catch (e: ApiException){
                    showMessageDialog(e.response.errorMsg)
                }catch (e: Exception){
                    toast(e.localizedMessage)
                }finally {
                    requireContext().hideKeyboard(binding.etTravelName.windowToken)
                }
            }
        }

        binding.ivNamingBack.setOnClickListener {
            requireContext().hideKeyboard(binding.etTravelName.windowToken)
            binding.tvNote.isVisible = true
            binding.clTravel.isVisible = true
            binding.clNamingTravel.isVisible = false
        }

    }

    private fun bindViewModel() {
        viewModel.currentTravel.observe(viewLifecycleOwner){
            adapter.setData(it)
        }

        viewModel.currentTravelCount.observe(viewLifecycleOwner){ count ->
            binding.ilTravelState.flTravelCount.isVisible = count > 0
            binding.ilTravelState.tvTravelCount.text = "$count"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.isMyTravelVisible.value = false
    }

    override fun onDelete(waypoint: TravelWayPoint) {
        viewModel.deleteTravel(waypoint)
        binding.root.postDelayed({
            adapter.notifyDataSetChanged()
        }, 20)
    }

    override fun onTouchMoveOrder(viewHolder: MyTravelAdapter.ViewHolder) {
        //觸發拖拉
        if (::touchHelper.isInitialized){
            touchHelper.startDrag(viewHolder)
        }
    }
}