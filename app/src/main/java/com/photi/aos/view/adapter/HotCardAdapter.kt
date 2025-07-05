package com.photi.aos.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photi.aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.photi.aos.view.fragment.photi.ChallengeCommendFragment
import com.photi.aos.viewmodel.PhotiViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HotCardAdapter(private val fragment: ChallengeCommendFragment, private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HotCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCardMissionSmallRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: HotCardAdapter.ViewHolder ,pos: Int) {
            with (photiViewModel.hotItems[pos]) {
                binding.cardLayout.layoutParams.width = convertDpToPixel(150, itemView.context)
                binding.titleTextView.text = name
                val formattedEnd = runCatching {
                    LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(DateTimeFormatter.ofPattern("~yyyy. M. d"))
                }.getOrDefault("")
                binding.dateTextView.text = formattedEnd

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

    // ViewHolder 생성, ViewHolder 는 View 를담는상자
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotCardAdapter.ViewHolder {
        val binding = ItemCardMissionSmallRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에데이터연결
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(viewHolder, position)

    }

    override fun getItemCount() = photiViewModel.hotItems.size

    // dp를 pixel 단위로 변환하는 함수
    private fun convertDpToPixel(dp: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}