package com.clockworkorange.shou.ui.tab.main.topmenu

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.FragmentSelectDestBinding
import com.clockworkorange.shou.ext.toPx
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SelectDestFragment : BaseFragment() {

    private var _binding: FragmentSelectDestBinding? = null
    private val binding get() = _binding!!

    private val cityTownViewModel: CityTownViewModel by viewModels { viewModelFactory }
    private val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectDestBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilTopMenu.tvMore.text = "目的地優惠推薦"
    }

    private fun initListener() {
        with(binding){
            ilTopMenuContent.flByArea.setOnClickListener {
                viewModel.clickSelectDataSource(MainTabViewModel.DataSourceOption.Area)
            }

            ilTopMenuContent.flByLocation.setOnClickListener {
                viewModel.clickSelectDataSource(MainTabViewModel.DataSourceOption.Location)
            }

            ilTopMenuContent.flByDest.setOnClickListener {
                viewModel.clickSelectDataSource(MainTabViewModel.DataSourceOption.Dest)
            }

            flCity.setOnClickListener {
                binding.llSelectCity.isVisible = true
            }

            flTown.setOnClickListener {
                if (!cityTownViewModel.isCitySelected()){
                    Toast.makeText(requireContext(), "請先選擇縣市", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                binding.llSelectTown.isVisible = true
            }

            flSearch.setOnClickListener {
                val address = binding.etAddress.editableText.toString()
                if (!cityTownViewModel.isCitySelected() || !cityTownViewModel.isTownSelected() || address.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "請先選擇縣市及區域並輸入地址", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val cityTown = cityTownViewModel.getSelectedCityTown()
                val dataSource = MainTabViewModel.DataSource.Dest(cityTown, address)
                viewModel.setDataSource(dataSource)
            }
        }
    }

    private fun bindViewModel() {

        cityTownViewModel.cityList.observe(viewLifecycleOwner){ list ->
            binding.llCityContainer.removeAllViews()
            list.forEach { name ->
                val item = createSelectItem(name)
                item.setOnClickListener(clickCityItemListener)
                binding.llCityContainer.addView(item)
                binding.llCityContainer.addView(createDivider())
            }

        }

        cityTownViewModel.townList.observe(viewLifecycleOwner){ list ->
            binding.tvSelectTown.text = "區域"
            binding.llTownContainer.removeAllViews()
            list.forEach { name ->
                val item = createSelectItem(name)
                item.setOnClickListener(clickTownItemListener)
                binding.llTownContainer.addView(item)
                binding.llTownContainer.addView(createDivider())
            }
        }

        viewModel.isTopMenuContentVisible.observe(viewLifecycleOwner){
            binding.ilTopMenuContent.root.isVisible = it
        }

    }

    private val clickCityItemListener = View.OnClickListener {
        val name = it.tag as String
        binding.tvSelectCity.text = name
        cityTownViewModel.selectCity(name)
        binding.llSelectCity.isVisible = false
    }

    private val clickTownItemListener = View.OnClickListener {
        val name = it.tag as String
        binding.tvSelectTown.text = name
        cityTownViewModel.selectTown(name)
        binding.llSelectTown.isVisible = false
    }

    private fun createSelectItem(name: String): View {
        return TextView(requireActivity()).apply {
            text = name
            tag = name
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_normal_text
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(34.toPx.toInt(), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 52.toPx.toInt())
        }
    }

    private fun createDivider(): View{
        return View(requireActivity()).apply {
            background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.color_top_menu_content_divider))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.toPx.toInt()).apply {
                setMargins(18.toPx.toInt(), 0, 18.toPx.toInt(), 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SelectDestFragment"
    }
}