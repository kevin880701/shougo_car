package com.clockworkorange.shou.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.databinding.FragmentPrivacyBinding
import com.clockworkorange.shou.ui.base.BaseFragment


class PrivacyFragment : BaseFragment() {

    companion object{
        fun newInstance(l: Listener) = PrivacyFragment().apply {
            listener = l
        }

        const val TAG = "Privacy"
    }

    interface Listener {
        fun onPrivacyRead()
    }

    private var listener: Listener? = null

    private var _binding: FragmentPrivacyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.btConfirm.setOnClickListener {
            listener?.onPrivacyRead()
        }
    }


}