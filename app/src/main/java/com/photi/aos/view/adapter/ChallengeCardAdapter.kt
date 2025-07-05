package com.photi.aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.photi.aos.databinding.ItemHomeChallengeRecyclerviewBinding
import com.photi.aos.viewmodel.PhotiViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChallengeCardAdapter(private val photiViewModel: PhotiViewModel,
                           private val onCardClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<ChallengeCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemHomeChallengeRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: ChallengeCardAdapter.ViewHolder, pos: Int) {
            with (photiViewModel.allItems[pos]) {
                binding.titleTextView.text = name
                binding.timeTextView.text = proveTime

                val formattedEnd = runCatching {
                    LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(DateTimeFormatter.ofPattern("yyyy. M. d"))
                }.getOrDefault("")
                binding.dateTextView.text = formattedEnd

                binding.chip1Btn.visibility = View.GONE
                binding.chip2Btn.visibility = View.GONE
                binding.chip3Btn.visibility = View.GONE

                hashtags.forEachIndexed { index, hashtag ->
                    when (index) {
                        0 -> {
                            binding.chip1Btn.text = hashtag.hashtag
                            binding.chip1Btn.visibility = View.VISIBLE
                        }
                        1 -> {
                            binding.chip2Btn.text = hashtag.hashtag
                            binding.chip2Btn.visibility = View.VISIBLE
                        }
                        2 -> {
                            binding.chip3Btn.text = hashtag.hashtag
                            binding.chip3Btn.visibility = View.VISIBLE
                        }
                    }
                }

                Glide.with(binding.imgView.context)
                    .load(challengeImageUrl)
                    .transform(CenterCrop(), RoundedCorners(12))
                    .into(binding.imgView)

                binding.root.setOnClickListener {
                    onCardClickListener(id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeCardAdapter.ViewHolder {
        val binding = ItemHomeChallengeRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(viewHolder,position)

    }

    override fun getItemCount() = photiViewModel.allItems.size

}