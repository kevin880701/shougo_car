package com.clockworkorange.shou.ui.tab.main

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.clockworkorange.shou.ShouGoService
import com.clockworkorange.shou.databinding.FragmentRequireLoginBinding
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.login.LoginViewModel
import com.clockworkorange.shou.util.QRCodeUtil
import timber.log.Timber

class RequireLoginFragment : BaseFragment() {

    interface Listener {
        fun noLogin()
    }

    companion object{
        const val TAG = "RequireLoginFragment"

        private const val KEY_SHOW_TOP_NOTE = "KEY_SHOW_TOP_NOTE"
        fun newInstance(showTopNote: Boolean = true, l: Listener) = RequireLoginFragment().apply {
            this.listener = l
            arguments = Bundle().apply {
                putBoolean(KEY_SHOW_TOP_NOTE, showTopNote)
            }
        }
    }

    private var _binding: FragmentRequireLoginBinding? = null
    private val binding get() = _binding!!

    private var listener: Listener? = null
    private val showTopNote: Boolean by lazy { arguments?.getBoolean(KEY_SHOW_TOP_NOTE) ?: true }

    private val viewModel: LoginViewModel by viewModels{viewModelFactory}

    private var qrCodeBitmap: Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequireLoginBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {
        binding.tvNote1.isInvisible = !showTopNote
    }

    private fun initListener() {
        binding.btNoLogin.setOnClickListener {
            if(ShouGoService.isGps && ShouGoService.isNetWork && ShouGoService.isGpsSignal){
                Timber.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                parentFragmentManager.popBackStack()
                listener?.noLogin()
            }else{
                Timber.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@898989")
//                Toast.makeText(context, "message.toString()", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindViewModel() {
        viewModel.loginQRCodeContent.observe(viewLifecycleOwner){ deviceId ->
            qrCodeBitmap = QRCodeUtil.createBitmap(deviceId)
            binding.ivQrCode.setImageBitmap(qrCodeBitmap)
        }

        viewModel.navigateMainScreen.observe(viewLifecycleOwner){
            parentFragmentManager.popBackStack()
        }

        viewModel.eventToast.observe(viewLifecycleOwner){
            toast(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        qrCodeBitmap?.recycle()
        qrCodeBitmap = null
        _binding = null
    }
}