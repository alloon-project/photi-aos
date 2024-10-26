package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.databinding.ItemFeedDefaultInBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.Comment
import com.example.alloon_aos.viewmodel.FeedInItem
interface OnFeedDeletedListener {
    fun onFeedDeleted(position: Int)
}

class FeedInAdapter(
    private val fragmentManager: FragmentManager,
    private val feedInItems: ArrayList<FeedInItem>
) : RecyclerView.Adapter<FeedInAdapter.ViewHolder>(), OnFeedDeletedListener {
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
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                    .into(binding.imgView)

                binding.feed.setOnClickListener {
                    FeedDetailDialog(pos,this@FeedInAdapter)
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
    override fun onFeedDeleted(position: Int) {
        feedInItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, feedInItems.size - position)
    }
}
