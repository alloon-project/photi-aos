package com.example.alloon_aos.view.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.alloon_aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HotCardAdapter(private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HotCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCardMissionSmallRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(holder: HotCardAdapter.ViewHolder ,pos: Int) {
            with (photiViewModel.hotItems[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date

                val multiOption = MultiTransformation(
                CenterCrop(),
                RoundedCorners(32),
                )

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(multiOption))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(a_resource: Drawable, a_transition: Transition<in Drawable>?) {
                            binding.smallcardLayout.background = a_resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
//            binding.root.setOnClickListener { // itemClickEvent는 MutableLiveData
//                viewModel.itemClickEvent.value = pos // itemClickEvent 옵저버에게 항목 번호와 클릭되었음을 알림
//            }
//            binding.root.setOnLongClickListener { // 뒤에 컨텍스트 메뉴를 위해 필요
//                viewModel.itemLongClick = pos // 롱클릭한 항목을 저장해 둠
//                false // for context menu
//            }
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

}