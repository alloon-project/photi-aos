package com.photi.aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.photi.aos.databinding.ItemCreateImageRecyclerviewBinding
import com.photi.aos.databinding.ItemSelectImageRecyclerviewBinding
import com.photi.aos.viewmodel.CreateViewModel

class ThumbnailAdapter(private val createViewModel: CreateViewModel,
                       private val onItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    companion object {
        private const val VIEW_TYPE_SELECT = 0
        private const val VIEW_TYPE_IMAGE = 1
    }

    inner class SelectViewHolder(private val binding: ItemSelectImageRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun setListener(pos : Int) {
                binding.root.setOnClickListener {
                    onItemClickListener(pos)
                }
            }
        }

    inner class ImageViewHolder(private val binding: ItemCreateImageRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (createViewModel.examImages[pos]) {
                Glide.with(binding.srcImageview.context)
                    .load(image)
                    .transform(CenterCrop(), RoundedCorners(24))
                    .into(binding.srcImageview)

                binding.imageTextview.setText(content)

                if (select)
                    binding.selectImageview.visibility = View.VISIBLE
                else
                    binding.selectImageview.visibility = View.INVISIBLE

                binding.root.setOnClickListener {
                    if (!select) {
                        createViewModel.select(pos)
                        createViewModel.setImageURL()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_SELECT else VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val binding = ItemCreateImageRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageViewHolder(binding)
            }
            else -> {
                val binding = ItemSelectImageRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SelectViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_IMAGE -> (viewHolder as ThumbnailAdapter.ImageViewHolder).setContents(position-1)
            VIEW_TYPE_SELECT -> (viewHolder as ThumbnailAdapter.SelectViewHolder).setListener(position)
        }
    }

    override fun getItemCount() = 5
}