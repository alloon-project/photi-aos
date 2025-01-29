package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.data.model.response.Feed
import com.example.alloon_aos.databinding.ItemFeedDefaultInBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation

interface OnFeedDeletedListener {
    fun onFeedDeleted(position: Int)
}

class FeedInAdapter(private val fragmentManager: FragmentManager):
    ListAdapter<Feed, FeedInAdapter.ViewHolder>(DiffCallback()), OnFeedDeletedListener {
    inner class ViewHolder(var binding: ItemFeedDefaultInBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Feed) {
            with(data) {
                binding.idTextView.text = username
                binding.timeTextView.text = createdDateTime

                //heart 여부
//                if (isClick) {
//                    binding.heartButton.setImageResource(R.drawable.ic_heart_filled)
//                    binding.heartButton.setColorFilter(R.color.gray700)
//                } else {
//                    binding.heartButton.setImageResource(R.drawable.ic_heart_empty)
//                    binding.heartButton.setColorFilter(R.color.gray700)
//                }

                Glide
                    .with(binding.imgView.context)
                    .load(imageUrl)
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                    .into(binding.imgView)

                binding.feed.setOnClickListener {
                    FeedDetailDialog(data.id,this@FeedInAdapter)
                        .show(fragmentManager,"FeedDetailDialog")
                }

//                binding.heartButton.setOnClickListener {
//                    isClick = !isClick
//                    if (isClick) {
//                        binding.heartButton.setImageResource(R.drawable.ic_heart_filled)
//                        binding.heartButton.setColorFilter(R.color.gray700)
//                    } else {
//                        binding.heartButton.setImageResource(R.drawable.ic_heart_empty)
//                        binding.heartButton.setColorFilter(R.color.gray700)
//                    }
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFeedDefaultInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedInAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onFeedDeleted(position: Int) {
        val currentList = currentList.toMutableList() // 현재 리스트를 변경 가능한 MutableList로 변환
        currentList.removeAt(position) // 해당 위치의 아이템 삭제
        submitList(currentList) // 새로운 리스트를 적용 (DiffUtil 자동 반영)
    }
}
