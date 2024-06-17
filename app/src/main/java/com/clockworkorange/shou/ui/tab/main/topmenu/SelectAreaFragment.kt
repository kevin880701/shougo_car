package com.clockworkorange.shou.ui.tab.main.topmenu

import android.annotation.SuppressLint
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
import com.clockworkorange.shou.databinding.FragmentSelectAreaBinding
import com.clockworkorange.shou.ext.*
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SelectAreaFragment : BaseFragment() {

    private var _binding: FragmentSelectAreaBinding? = null
    private val binding get() = _binding!!

    private val cityTownViewModel: CityTownViewModel by viewModels { viewModelFactory }
    private val viewModel: MainTabViewModel by viewModels({requireParentFragment()}, { viewModelFactory })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAreaBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.ilTopMenu.tvMore.text = "地區優惠推薦"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {

        with(binding){

            root.setOnClickListener {
                //ignore
            }

            viewTouch.setOnTouchListener { v, event ->
                val x = event.x
                val y = event.y

                if (binding.llSelectCity.isVisible && !binding.llSelectCity.getViewRect().contains(x, y)){
                    binding.llSelectCity.isVisible = false
                    return@setOnTouchListener true
                }

                if (binding.llSelectTown.isVisible && !binding.llSelectTown.getViewRect().contains(x, y)){
                    binding.llSelectTown.isVisible = false
                    return@setOnTouchListener true
                }

                if (binding.ilTopMenuContent.root.isVisible && !binding.ilTopMenuContent.root.getViewRect().contains(x, y)){
                    binding.ilTopMenuContent.root.isVisible = false
                    return@setOnTouchListener true
                }

                return@setOnTouchListener false

            }

            ilTopMenu.root.setOnClickListener {
                ilTopMenuContent.root.isVisible = true
            }

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
                if (!cityTownViewModel.isCitySelected() || !cityTownViewModel.isTownSelected()){
                    Toast.makeText(requireContext(), "請先選擇縣市及區域", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val cityTown = cityTownViewModel.getSelectedCityTown()
                val dataSource = MainTabViewModel.DataSource.Area(cityTown)
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
            letterSpacing = 0.1f
            setTextColor(getColor(R.color.color_normal_text))
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

    override fun onNightModeChange(isNight: Boolean) {

        binding.root.setBackgroundColor(getColor(R.color.color_background))

        binding.flCity.background = getDrawable(R.drawable.bg_category)
        binding.tvSelectCity.setTextColor(getColor(R.color.color_primary))
        binding.ivCityTriangle.setColorFilter(getColor(R.color.color_primary))

        binding.flTown.background = getDrawable(R.drawable.bg_category)
        binding.tvSelectTown.setTextColor(getColor(R.color.color_primary))
        binding.ivTownTriangle.setColorFilter(getColor(R.color.color_primary))

        binding.llSelectCity.background = getDrawable(R.drawable.bg_category_content)
        binding.llSelectTown.background = getDrawable(R.drawable.bg_category_content)

        cityTownViewModel.refresh()

        binding.ilTopMenu.refresh()
        binding.ilTopMenuContent.refresh()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SelectAreaFragment"
    }
}