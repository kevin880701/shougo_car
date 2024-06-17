package com.clockworkorange.shou.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.clockworkorange.shou.R


class MessageDialogFragment : DialogFragment() {

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_MESSAGE = "KEY_MESSAGE"
        fun newInstance(title: String?, message: String?, confirmCallback: (() -> Unit)? = null) =
            MessageDialogFragment().apply {
                this.confirmCallback = confirmCallback
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_MESSAGE, message)
                }
            }
    }

    private val title: String? by lazy { requireArguments().getString(KEY_TITLE) }
    private val message: String? by lazy { requireArguments().getString(KEY_MESSAGE) }
    private var confirmCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message_dialog, container, false)
        view.findViewById<TextView>(R.id.tv_title)?.apply {
            text = title
            visibility = if (title == null) View.GONE else View.VISIBLE
        }

        view.findViewById<TextView>(R.id.tv_msg)?.apply {
            text = message
            visibility = if (message == null) View.GONE else View.VISIBLE
        }

        view.findViewById<Button>(R.id.bt_confirm).setOnClickListener {
            confirmCallback?.invoke()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}