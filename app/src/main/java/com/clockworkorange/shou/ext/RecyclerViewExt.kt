package com.clockworkorange.shou.ext

import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.shou.ui.custom.VerticalSpaceItemDecoration


fun RecyclerView.addVerticalSpace(space: Int){
    addItemDecoration(VerticalSpaceItemDecoration(space))
}