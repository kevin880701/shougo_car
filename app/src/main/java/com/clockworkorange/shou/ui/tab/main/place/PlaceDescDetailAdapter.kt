package com.clockworkorange.shou.ui.tab.main.place

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clockworkorange.repository.model.StoreDesc
import com.clockworkorange.shou.databinding.ItemPlaceDescDetailBinding
import com.clockworkorange.shou.ext.loadImage

private val StoreDescDiffUtil = object : DiffUtil.ItemCallback<StoreDesc>() {
    override fun areItemsTheSame(oldItem: StoreDesc, newItem: StoreDesc): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: StoreDesc, newItem: StoreDesc): Boolean {
        return oldItem.description == newItem.description
    }
}

class PlaceDescDetailAdapter :
    ListAdapter<StoreDesc, PlaceDescDetailAdapter.ViewHolder>(StoreDescDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceDescDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPlaceDescDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding.webYoutube){
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.domStorageEnabled = true
            }
        }

        fun bind(item: StoreDesc) {
            binding.tvDesc.text = item.description

            if (item.imageUrl.isNotEmpty()) {
                binding.ivImage.loadImage(item.imageUrl)
            }

            item.getYoutubeId()?.let { ytId ->
                binding.flYoutube.isVisible = true
                val s = """
                    <html><body style="padding: 0; margin: 0;">
                    <iframe width=100% height=100% src="https://www.youtube.com/embed/$ytId" frameborder="0"></iframe>
                    </body></html>
                """.trimIndent()

                binding.webYoutube.loadData(s, "text/html", "utf-8")
            } ?: kotlin.run {
                binding.flYoutube.isVisible = false
            }

        }

    }
}