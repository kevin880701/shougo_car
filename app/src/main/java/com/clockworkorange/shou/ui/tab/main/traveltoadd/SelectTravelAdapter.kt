package com.clockworkorange.shou.ui.tab.main.traveltoadd

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.shou.databinding.ListitemTitleBinding
import com.clockworkorange.shou.util.AdapterListener

class SelectTravelAdapter(private val listener: Listener): RecyclerView.Adapter<SelectTravelAdapter.ViewHolder>() {

    interface Listener: AdapterListener<ShouTravel>{
        fun addToCurrentTravel()
    }

    val data = mutableListOf<ShouTravel>()

    override fun getItemCount(): Int = data.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemTitleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount-1){
            holder.setAddCurrent()
        }else{
            holder.bind(data[position])
        }
    }

    inner class ViewHolder(private val binding: ListitemTitleBinding): RecyclerView.ViewHolder(binding.root){
        init {
              binding.root.setOnClickListener {
                  if (bindingAdapterPosition == itemCount-1){
                      listener.addToCurrentTravel()
                  }else{
                      listener.onItemClick(data[bindingAdapterPosition])
                  }
              }
          }

        fun bind(shouTravel: ShouTravel) {
            binding.tvTitle.text = shouTravel.name
        }

        fun setAddCurrent() {
            binding.tvTitle.text = "+我的旅程"
        }
    }
}