package com.clockworkorange.shou.ui.tab.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.clockworkorange.repository.domain.AccountState
import com.clockworkorange.shou.R
import com.clockworkorange.shou.databinding.AccountFragmentBinding
import com.clockworkorange.shou.ext.getColor
import com.clockworkorange.shou.ext.getDrawable
import com.clockworkorange.shou.ui.base.BaseFragment
import com.clockworkorange.shou.ui.tab.main.RequireLoginFragment
import com.clockworkorange.shou.util.GenericDialogListener

class  AccountFragment : BaseFragment() {

    companion object{
        const val TAG = "AccountFragment"
    }

    private val viewModel: AccountViewModel by viewModels { viewModelFactory }

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AccountFragmentBinding.inflate(layoutInflater)
        initView()
        initListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.tvLogout.setOnClickListener {
            RequireLogoutFragment.newInstance(object : GenericDialogListener{
                override fun onCancel() {}

                override fun onConfirm() {
                    viewModel.logout()
                }
            }).show(childFragmentManager, RequireLogoutFragment.TAG)
        }

        binding.tvLogin.setOnClickListener {
            val fragment = RequireLoginFragment.newInstance(false, object : RequireLoginFragment.Listener{
                override fun noLogin() {}
            })
            childFragmentManager.commit {
                add(R.id.fl_container, fragment, RequireLoginFragment.TAG)
                addToBackStack("")
            }
        }
    }

    private fun bindViewModel() {
        viewModel.accountState.observe(viewLifecycleOwner){ state ->
            when(state){
                is AccountState.LoggedIn -> {
                    binding.tvAccount.text = state.data.email
                    binding.tvAccount.isVisible = true
                    binding.tvNotLogin.isVisible = false
                    binding.tvLogin.isVisible = false
                    binding.tvLogout.isVisible = true
                }
                AccountState.NotLogin -> {
                    binding.tvAccount.text = ""
                    binding.tvAccount.isVisible = false
                    binding.tvNotLogin.isVisible = true
                    binding.tvLogin.isVisible = true
                    binding.tvLogout.isVisible = false
                }
            }
        }

    }

    override fun onNightModeChange(isNight: Boolean) {
        binding.root.setBackgroundColor(getColor(R.color.color_background))
        binding.tvMyAccountTitle.setTextColor(getColor(R.color.color_secondary))
        binding.ivAccountBg.setBackgroundColor(getColor(R.color.color_secondary))
        binding.tvAccountTitle.setTextColor(getColor(R.color.color_normal_text))
        binding.tvAccount.setTextColor(getColor(R.color.color_normal_text))

        binding.tvNotLogin.setTextColor(getColor(R.color.color_account_text_hint))
        binding.tvLogout.setTextColor(getColor(R.color.color_primary))
        binding.tvLogout.background = getDrawable(R.drawable.bg_logout_button)

        binding.tvLogin.setTextColor(getColor(R.color.white))
        binding.tvLogin.background = getDrawable(R.drawable.bg_round_c30f23)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}