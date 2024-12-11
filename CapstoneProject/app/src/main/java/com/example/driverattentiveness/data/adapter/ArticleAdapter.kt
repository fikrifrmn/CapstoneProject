package com.example.driverattentiveness.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.driverattentiveness.R
import com.example.driverattentiveness.data.api.response.ArticlesItem
import com.example.driverattentiveness.databinding.ItemRowArticleListBinding

class ArticleAdapter : ListAdapter<ArticlesItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: ItemRowArticleListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            binding.tvContent.text = article.title
            val imageUrl = article.urlToImage
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.ivHeader)
            } else {
                Glide.with(binding.root.context)
                    .load(R.drawable.baseline_placeholder)
                    .into(binding.ivHeader)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowArticleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
