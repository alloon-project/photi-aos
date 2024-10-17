package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.ItemRuleChipRecyclerviewBinding
import com.example.alloon_aos.viewmodel.JoinViewModel

class RuleHashAdapter(private val joinViewModel: JoinViewModel) :
    RecyclerView.Adapter<RuleHashAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemRuleChipRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setContents(pos: Int) {
            with (joinViewModel.hashs[pos]) {
                binding.hashBtn.text = chip

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleHashAdapter.ViewHolder {
        val binding = ItemRuleChipRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)
    }

    override fun getItemCount() = joinViewModel.hashs.size

}