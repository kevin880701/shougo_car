package com.clockworkorange.shou.ui.tab.packet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.databinding.FragmentPacketBinding
import com.clockworkorange.shou.ui.base.BaseFragment

class PacketFragment : BaseFragment() {

    companion object {
        const val TAG = "PacketFragment"
    }

    private var _binding: FragmentPacketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPacketBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

    }

    private fun initListener() {

    }

    private fun bindViewModel() {


    }

}