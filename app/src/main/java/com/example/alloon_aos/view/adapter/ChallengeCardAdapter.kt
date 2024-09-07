package com.example.alloon_aos.view.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.alloon_aos.databinding.ItemHomeChallengeRecyclerviewBinding
import com.example.alloon_aos.view.ui.util.CustomRotationTransformation
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ChallengeCardAdapter(private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<ChallengeCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemHomeChallengeRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: ChallengeCardAdapter.ViewHolder, pos: Int) {
            with (photiViewModel.proofItems[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date
                binding.timeTextView.text = time

//                binding.chip1Btn.text = hashtag[0]
//                binding.chip1Btn.visibility = View.VISIBLE
//                if(hashtag.size == 2){
//                    binding.chip2Btn.text = hashtag[1]
//                    binding.chip2Btn.visibility = View.VISIBLE
//                }
                binding.chip1Btn.visibility = View.GONE
                binding.chip2Btn.visibility = View.GONE
                binding.chip3Btn.visibility = View.GONE

                hashtag.forEachIndexed { index, hashtag ->
                    when (index) {
                        0 -> {
                            binding.chip1Btn.text = hashtag
                            binding.chip1Btn.visibility = View.VISIBLE
                        }
                        1 -> {
                            binding.chip2Btn.text = hashtag
                            binding.chip2Btn.visibility = View.VISIBLE
                        }
                        2 -> {
                            binding.chip3Btn.text = hashtag
                            binding.chip3Btn.visibility = View.VISIBLE
                        }
                    }
                }

                val multiOption = MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(6),
                )

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(multiOption))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(a_resource: Drawable, a_transition: Transition<in Drawable>?) {
                            binding.cardLayout.background = a_resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
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

    override fun getItemCount() = photiViewModel.proofItems.size

}