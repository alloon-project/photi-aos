package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemProofshotViewpagerBinding
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.ProofShotItem
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ProofShotHomeAdapter(private val photiViewModel: PhotiViewModel,
                           private var proofItems: List<ProofShotItem>,
                           private val onItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<ProofShotHomeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemProofshotViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (photiViewModel.proofItems[pos]) {
                binding.bannerTextview.setText(title)
                if (proof) {
                    binding.timeTextview.visibility = View.GONE
                    binding.doneTextview.visibility = View.VISIBLE
                    binding.proofshotButton.visibility = View.GONE
                    binding.proofshotImageview.visibility = View.VISIBLE
                    binding.dividerBlue.visibility = View.GONE
                    if(pos != itemCount-1) {
                        binding.dividerGreen.visibility = View.VISIBLE
                    }
                    binding.bannerFramelayout.setBackgroundResource(R.drawable.banner_green)
                    binding.bannerImageview.setImageResource(R.drawable.graphic_clover)
                    binding.proofshotFramelayout.setBackgroundResource(R.drawable.proofshot_green)
                    binding.proofshotShape.setImageResource(R.drawable.graphic_shape_green200)

                    Glide.with(binding.proofshotImageview.context)
                        .load(url)
                        .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                        .into(binding.proofshotImageview)
                } else {
                    binding.timeTextview.setText(time)
                    binding.proofshotButton.setOnClickListener {
                        onItemClickListener(pos)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProofShotHomeAdapter.ViewHolder {
        val binding = ItemProofshotViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount() = photiViewModel.proofItems.size

    fun updatePhotoItems(newPhotoItems: List<ProofShotItem>) {
        proofItems = newPhotoItems
        notifyDataSetChanged()
    }
}