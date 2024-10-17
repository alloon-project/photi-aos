package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.ItemCreateChipRecyclerviewBinding
import com.example.alloon_aos.viewmodel.CreateViewModel

class AddHashAdapter(private val createViewModel: CreateViewModel) :
    RecyclerView.Adapter<AddHashAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCreateChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (createViewModel._hashs[pos]) {
                binding.hashtagTextveiw.text = this
                binding.deleteBtn.setOnClickListener {
                    createViewModel.deleteHash(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddHashAdapter.ViewHolder {
        val binding = ItemCreateChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)
    }

    override fun getItemCount() = createViewModel._hashs.size

}