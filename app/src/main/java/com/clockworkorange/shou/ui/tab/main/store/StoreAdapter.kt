package com.clockworkorange.shou.ui.tab.main.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.domain.VoiceStore
import com.clockworkorange.shou.databinding.ListitemVoiceStoreBinding
import com.clockworkorange.shou.ext.loadImage
import com.clockworkorange.shou.util.AdapterListener

class StoreAdapter(private val listener: AdapterListener<VoiceStore>): RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    var data = listOf<VoiceStore>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListitemVoiceStoreBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val binding: ListitemVoiceStoreBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.viewBg.setOnClickListener {
                listener.onItemClick(data[bindingAdapterPosition])
            }
        }

        fun bind(store: VoiceStore){
            with(binding){
                tvTitle.text = store.name
                ivLogo.loadImage(store.logoUrl)
            }
        }

    }
}