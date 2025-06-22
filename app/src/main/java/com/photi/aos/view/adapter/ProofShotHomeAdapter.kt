package com.photi.aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.photi.aos.databinding.ItemProofshotCompleteViewpagerBinding
import com.photi.aos.databinding.ItemProofshotViewpagerBinding
import com.photi.aos.view.ui.util.RoundedCornersTransformation
import com.photi.aos.viewmodel.PhotiViewModel

class ProofShotHomeAdapter(private val photiViewModel: PhotiViewModel,
                           private val onProofItemClickListener: (Int) -> Unit,
                           private val onCompleteClickListener: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROOFSHOT = 0
        private const val VIEW_TYPE_COMPLETE = 1
    }

    inner class ProofshotViewHolder(private val binding: ItemProofshotViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (photiViewModel.proofItems[pos]) {
                if(pos == photiViewModel.proofItems.size-1 && photiViewModel.completeItems.size == 0)
                    binding.dividerBlue.visibility = View.GONE
                else
                    binding.dividerBlue.visibility = View.VISIBLE

                binding.bannerTextview.setText(name)
                binding.timeTextview.setText(proveTime)

                binding.proofshotButton.setOnClickListener {
                    photiViewModel.updateCurrentItem(this)
                    photiViewModel.id = id
                    onProofItemClickListener(id)
                }
            }
        }
    }

    inner class CompleteViewHolder(private val binding: ItemProofshotCompleteViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (photiViewModel.completeItems[pos]) {
                binding.bannerTextview.setText(name)
                if(pos == photiViewModel.completeItems.size-1)
                    binding.dividerGreen.visibility = View.GONE
                else
                    binding.dividerGreen.visibility = View.VISIBLE

                Glide.with(binding.proofshotImageview.context)
                    .load(feedImageUrl)
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                    .into(binding.proofshotImageview)

                binding.proofshotImageview.setOnClickListener {
                    photiViewModel.id = id
                    photiViewModel.feedId = feedId!!
                    onCompleteClickListener()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < photiViewModel.proofItems.size) VIEW_TYPE_PROOFSHOT else VIEW_TYPE_COMPLETE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROOFSHOT -> {
                val binding = ItemProofshotViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProofshotViewHolder(binding)
            }
            else -> {
                val binding = ItemProofshotCompleteViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CompleteViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_PROOFSHOT -> (viewHolder as ProofshotViewHolder).setContents(position)
            VIEW_TYPE_COMPLETE -> (viewHolder as CompleteViewHolder).setContents(position-photiViewModel.proofItems.size)
        }
    }

    override fun getItemCount() = photiViewModel.proofItems.size + photiViewModel.completeItems.size

}