package com.example.alloon_aos.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemHashtagChipRecyclerviewBinding
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HashTagAdapter(private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HashTagAdapter.ViewHolder>() {
    private var gray  = R.color.gray800

    inner class ViewHolder(private val binding: ItemHashtagChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun setContents(pos: Int) {
            with (photiViewModel.hashChips[pos]) {
                binding.hashtagTextveiw.text = id
                binding.chipLayout.setBackgroundResource(R.drawable.chip_gray)
                binding.hashtagTextveiw.setTextColor(gray)

            }
            binding.root.setOnClickListener { // itemClickEvent는 MutableLiveData
                photiViewModel.itemClickEvent.value = pos // itemClickEvent 옵저버에게 항목 번호와 클릭되었음을 알림
            }
//            binding.root.setOnLongClickListener { // 뒤에 컨텍스트 메뉴를 위해 필요
//                viewModel.itemLongClick = pos // 롱클릭한 항목을 저장해 둠
//                false // for context menu
//            }
        }
    }

    // ViewHolder 생성, ViewHolder 는 View 를담는상자
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagAdapter.ViewHolder {
        val binding = ItemHashtagChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에데이터연결
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)

    }

    override fun getItemCount() = photiViewModel.hashChips.size

}