package com.clockworkorange.shou.ui.tab.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.databinding.FragmentRequireNavigationParkBinding
import com.clockworkorange.shou.ext.removeByTag
import com.clockworkorange.shou.ui.base.BaseFragment

class RequireNavigationParkFragment : BaseFragment() {

    interface Listener {
        fun onRequireNavigationParkResult(requirePark: Boolean)
    }

    companion object {
        const val TAG = "RequireNavigationParkFragment"
        private const val KEY_TITLE = "KEY_TITLE"

        fun newInstance(title: String, l: Listener) = RequireNavigationParkFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_TITLE, title)
            }
            this.listener = l
        }
    }

    private var _binding: FragmentRequireNavigationParkBinding? = null
    private val binding get() = _binding!!
    private var listener: Listener? = null
    private val title by lazy { arguments?.getString(KEY_TITLE) ?: "--" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequireNavigationParkBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {
        binding.tvTitle.text = title
    }

    private fun initListener() {
        binding.ilButtonYes.root.setOnClickListener {
            listener?.onRequireNavigationParkResult(true)
            parentFragmentManager.removeByTag(RequireNavigationParkFragment.TAG)
        }

        binding.ilButtonNo.root.setOnClickListener {
            listener?.onRequireNavigationParkResult(false)
            parentFragmentManager.removeByTag(RequireNavigationParkFragment.TAG)

        }

        binding.ivClose.setOnClickListener {
            parentFragmentManager.removeByTag(RequireNavigationParkFragment.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}