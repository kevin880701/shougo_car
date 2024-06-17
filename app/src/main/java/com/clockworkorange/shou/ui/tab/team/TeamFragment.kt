package com.clockworkorange.shou.ui.tab.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ui.base.BaseFragment

class TeamFragment : BaseFragment() {

    companion object {
        const val TAG = "TeamFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team, container, false)
    }

}