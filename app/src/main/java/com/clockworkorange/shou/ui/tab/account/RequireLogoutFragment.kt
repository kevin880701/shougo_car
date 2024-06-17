package com.clockworkorange.shou.ui.tab.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.clockworkorange.shou.databinding.FragmentRequireLogoutBinding
import com.clockworkorange.shou.util.GenericDialogListener

class RequireLogoutFragment : DialogFragment() {

    companion object {
        const val TAG = "RequireLogoutFragment"
        fun newInstance(l:GenericDialogListener) = RequireLogoutFragment().apply {
                this.listener = l
            }
    }

    private var _binding: FragmentRequireLogoutBinding? = null
    private val binding get() = _binding!!

    private var listener: GenericDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentRequireLogoutBinding.inflate(layoutInflater)
        initListener()
        return binding.root
    }

    private fun initListener() {
        binding.ilYes.root.setOnClickListener {
            listener?.onConfirm()
            dismiss()
        }

        binding.ilNo.root.setOnClickListener {
            listener?.onCancel()
            dismiss()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}