package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.ItemHashtagCardRecyclerviewBinding
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HashCardAdapter(private val photiViewModel: PhotiViewModel) :
    RecyclerView.Adapter<HashCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemHashtagCardRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (photiViewModel.hotItems[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashCardAdapter.ViewHolder {
        val binding = ItemHashtagCardRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에데이터연결
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)

    }

    override fun getItemCount() = photiViewModel.hotItems.size

}