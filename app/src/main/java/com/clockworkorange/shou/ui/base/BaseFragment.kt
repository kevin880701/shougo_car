package com.clockworkorange.shou.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import com.clockworkorange.shou.APP
import com.clockworkorange.shou.AppViewModelFactory
import com.clockworkorange.shou.ui.MainActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

open class BaseFragment: Fragment() {

    val viewModelFactory: AppViewModelFactory
        get() = (requireContext().applicationContext as APP).appContainer.viewModelFactory


    protected val genericExceptionHandler = CoroutineExceptionHandler{ _, e ->
        toast(e.localizedMessage)
    }

    init {
        lifecycleScope.launchWhenStarted {
            APP.nightMode.distinctUntilChanged().observe(viewLifecycleOwner){ onNightModeChange(it) }
        }
    }

    protected open fun onNightModeChange(isNight: Boolean){}

    protected fun toast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    protected fun removeSelf(){
        parentFragmentManager.commit { remove(this@BaseFragment) }
    }

    protected fun showMessageDialog(msg: String, title: String? = null){
        (requireActivity() as? BaseActivity)?.showConfirmDialog(msg, title)
    }

    protected fun showLoading(){
        (requireActivity() as? MainActivity)?.showLoading()
    }

    protected fun hideLoading(){
        (requireActivity() as? MainActivity)?.hideLoading()
    }

    private var logLifeCycle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (logLifeCycle) Timber.tag("fragmentLife").d("onCreate $this")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (logLifeCycle) Timber.tag("fragmentLife").d("onViewCreated $this")
    }

    override fun onStart() {
        super.onStart()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onStart $this")
    }

    override fun onResume() {
        super.onResume()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onResume $this")
    }

    override fun onPause() {
        super.onPause()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onPause $this")
    }

    override fun onStop() {
        super.onStop()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onStop $this")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onDestroyView $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (logLifeCycle) Timber.tag("fragmentLife").d("onDestroy $this")
    }

}