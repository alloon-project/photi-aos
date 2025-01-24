package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.databinding.ItemCardMissionLargeRecyclerviewBinding
import com.example.alloon_aos.view.fragment.photi.ChallengeCommendFragment
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HashCardAdapter(private val fragment: ChallengeCommendFragment, private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HashCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCardMissionLargeRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: HashCardAdapter.ViewHolder, pos: Int) {
            with (photiViewModel.hashItems[pos]) {
                binding.titleTextView.text = name
                binding.dateTextView.text = endDate

                if(hashtags.isNotEmpty()) {
                    hashtags.forEachIndexed { index, hashtag ->
                        when (index) {
                            0 -> {
                                binding.chip1Btn.text = hashtag.hashtag
                                binding.chip1Btn.visibility = View.VISIBLE
                                binding.chip2Btn.visibility = View.GONE
                                binding.chip3Btn.visibility = View.GONE
                            }
                            1 -> {
                                binding.chip2Btn.text = hashtag.hashtag
                                binding.chip2Btn.visibility = View.VISIBLE
                                binding.chip3Btn.visibility = View.GONE
                            }
                            2 -> {
                                binding.chip3Btn.text = hashtag.hashtag
                                binding.chip3Btn.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                Glide
                    .with(holder.itemView.context)
                    .load(imageUrl)
                    .into(binding.imgView)

                binding.root.setOnClickListener {
                    photiViewModel.id = id
                    fragment.setOnclick()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashCardAdapter.ViewHolder {
        val binding = ItemCardMissionLargeRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(viewHolder,position)
    }

    override fun getItemCount() = photiViewModel.hashItems.size

}