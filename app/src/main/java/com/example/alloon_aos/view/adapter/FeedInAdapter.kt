package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.databinding.ItemFeedDefaultInBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog
import com.example.alloon_aos.viewmodel.Comment
import com.example.alloon_aos.viewmodel.FeedInItem

class FeedInAdapter(private val fragmentManager: FragmentManager, val feedInItems: ArrayList<FeedInItem>): RecyclerView.Adapter<FeedInAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: ItemFeedDefaultInBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: ViewHolder, pos: Int) {
            with(feedInItems[pos]) {
                binding.idTextView.text = id
                binding.timeTextView.text = time

                if (isClick) {
                    binding.heartButton.setImageResource(R.drawable.ic_heart_filled)
                    binding.heartButton.setColorFilter(R.color.gray700)
                } else {
                    binding.heartButton.setImageResource(R.drawable.ic_heart_empty)
                    binding.heartButton.setColorFilter(R.color.gray700)
                }

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .into(binding.imgView)

                binding.feed.setOnClickListener {
                    FeedDetailDialog(pos)
                        .show(fragmentManager,"tag")
                }

                binding.heartButton.setOnClickListener {
                    isClick = !isClick
                    if (isClick) {
                        binding.heartButton.setImageResource(R.drawable.ic_heart_filled)
                        binding.heartButton.setColorFilter(R.color.gray700)
                    } else {
                        binding.heartButton.setImageResource(R.drawable.ic_heart_empty)
                        binding.heartButton.setColorFilter(R.color.gray700)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFeedDefaultInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(viewHolder, position)
    }

    override fun getItemCount() = feedInItems.size
}
