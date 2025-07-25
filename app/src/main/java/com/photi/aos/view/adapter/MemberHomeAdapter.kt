package com.photi.aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.photi.aos.databinding.ItemGraphicViewpagerBinding
import com.photi.aos.databinding.ItemPhotoViewpagerBinding
import com.photi.aos.viewmodel.PhotiViewModel

class MemberHomeAdapter( private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_PHOTO = 0
        private const val VIEW_TYPE_GRAPHIC = 1
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (photiViewModel.hotItems[pos]) {
                Glide.with(binding.imageView.context)
                    .load(imageUrl)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(binding.imageView)
            }
        }
    }

    inner class GraphicViewHolder(private val binding: ItemGraphicViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == photiViewModel.hotItems.size) VIEW_TYPE_GRAPHIC else VIEW_TYPE_PHOTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRAPHIC -> {
                val binding = ItemGraphicViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GraphicViewHolder(binding)
            }
            else -> {
                val binding = ItemPhotoViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PhotoViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_PHOTO -> (holder as PhotoViewHolder).setContents(position)
        }
    }

    override fun getItemCount() = photiViewModel.hotItems.size + 1 // +1 for the graphic item

}