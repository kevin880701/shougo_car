package com.clockworkorange.shou.ui.tab.favorite.travel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.databinding.FragmentRequireDeleteTravelBinding
import com.clockworkorange.shou.ui.base.BaseFragment


class RequireDeleteTravelFragment : BaseFragment() {

    companion object{
        const val TAG = "RequireDeleteTravelFragment"

        fun newInstance(callback: (Boolean) -> Unit) = RequireDeleteTravelFragment().apply {
            this.callback = callback
        }
    }

    private var _binding: FragmentRequireDeleteTravelBinding? = null
    private val binding get() = _binding!!

    private var callback: ((Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequireDeleteTravelBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {

        binding.root.setOnClickListener {

        }

        binding.ilButtonYes.root.setOnClickListener {
            callback?.invoke(true)
            parentFragmentManager.popBackStack()
        }

        binding.ilButtonNo.root.setOnClickListener {
            callback?.invoke(false)
            parentFragmentManager.popBackStack()
        }

        binding.ivClose.setOnClickListener {
            callback?.invoke(false)
            parentFragmentManager.popBackStack()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}