package com.example.alloon_aos.view.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
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
                binding.cardLayout.layoutParams.width = convertDpToPixel(150, itemView.context)
                binding.titleTextView.text = title
                binding.dateTextView.text = date
                binding.chip1Btn.text = hashtag[0]
                binding.chip1Btn.visibility = View.VISIBLE
                if(hashtag.size == 2){
                    binding.chip2Btn.text = hashtag[1]
                    binding.chip2Btn.visibility = View.VISIBLE
                }

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .into(binding.imgView)
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

    // dp를 pixel 단위로 변환하는 함수
    private fun convertDpToPixel(dp: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}