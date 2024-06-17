package com.clockworkorange.shou.ui.login

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.clockworkorange.shou.databinding.FragmentRequirePermissionBinding
import com.clockworkorange.shou.ui.base.BaseFragment


class RequirePermissionFragment : BaseFragment() {

    companion object{
        fun newInstance(l: Listener) = RequirePermissionFragment().apply {
            listener = l
        }

        const val TAG = "RequirePermission"
    }

    interface Listener {
        fun onPermissionGranted()
    }

    private var listener: Listener? = null

    private var _binding: FragmentRequirePermissionBinding? = null
    private val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                listener?.onPermissionGranted()
            } else {
//                showNeedPermissionDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequirePermissionBinding.inflate(layoutInflater)
        initListener()

        return binding.root
    }

    private fun initListener() {
        binding.btConfirm.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            // 检查是否已经授予 ACCESS_FINE_LOCATION 权限
//            if (activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // 如果权限尚未授予，请求 ACCESS_FINE_LOCATION 权限
//                ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
//            }
//
//// 检查是否已经授予 ACCESS_COARSE_LOCATION 权限
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // 如果权限尚未授予，请求 ACCESS_COARSE_LOCATION 权限
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 200)
//            }
        }
    }
}