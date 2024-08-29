package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.alloon_aos.databinding.ItemGuestPhotoRecyclerviewBinding
import com.example.alloon_aos.view.ui.util.CustomRotationTransformation
import com.example.alloon_aos.viewmodel.PhotiViewModel

class GuestHomeAdapter(private val photiViewModel: PhotiViewModel):
    RecyclerView.Adapter<GuestHomeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemGuestPhotoRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
                with (photiViewModel.photoItems[pos]) {

                    var degree = 0f
                    when(pos){
                        0 -> degree = -2f
                        1 -> degree = 1f
                        2 -> degree = 2f
                        3 -> degree = 1f
                        4 -> degree = -2f
                        5 -> degree = -1f
                    }

                    Glide.with(binding.imageView.context)
                        .load(url)
                        .transform(CenterCrop(), RoundedCorners(8), CustomRotationTransformation(degree))
                        .into(binding.imageView)

                    if (pos == 1 || pos == 4) {
                        val layoutParams = binding.imageView.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.topMargin = 20
                        binding.imageView.layoutParams = layoutParams
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGuestPhotoRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuestHomeAdapter.ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount() = photiViewModel.photoItems.size

}