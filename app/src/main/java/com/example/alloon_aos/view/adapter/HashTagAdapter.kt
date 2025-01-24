package com.example.alloon_aos.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemHashtagChipRecyclerviewBinding
import com.example.alloon_aos.view.fragment.photi.ChallengeCommendFragment
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HashTagAdapter(private val mContext : Context,
                     private val fragment: ChallengeCommendFragment,
                     private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HashTagAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHashtagChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (photiViewModel.hashChips[pos]) {
                binding.hashtagTextveiw.text = hash

                if (select) {
                    binding.chipLayout.setBackgroundResource(R.drawable.chip_blue)
                    binding.hashtagTextveiw.setTextColor(mContext.getColor(R.color.blue500))
                } else {
                    binding.chipLayout.setBackgroundResource(R.drawable.chip_gray_line)
                    binding.hashtagTextveiw.setTextColor(mContext.getColor(R.color.gray800))
                }

                binding.root.setOnClickListener {
                    fragment.clickOneChip()
                    photiViewModel.clickOneChip(pos, hash)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagAdapter.ViewHolder {
        val binding = ItemHashtagChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)
    }

    override fun getItemCount() = photiViewModel.hashChips.size

}