package com.example.alloon_aos.view.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HotCardAdapter(private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HotCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCardMissionSmallRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (photiViewModel.hotItems[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date
                //binding.hotcardLayout.setBackgroundResource(R.color.black)

//                val bgShape = binding.hotcardLayout.background as GradientDrawable
//                bgShape.setColor(Color.parseColor("#22aadd"))
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
        viewHolder.setContents(position)

    }

    override fun getItemCount() = photiViewModel.hotItems.size

}