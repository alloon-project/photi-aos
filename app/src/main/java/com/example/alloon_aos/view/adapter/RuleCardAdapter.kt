package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.ItemInquiryRuleRecyclerviewBinding
import com.example.alloon_aos.viewmodel.InquiryViewModel

class RuleCardAdapter(private val inquiryViewModel: InquiryViewModel) :
    RecyclerView.Adapter<RuleCardAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemInquiryRuleRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (inquiryViewModel.rules[pos]) {
                binding.ruleTextview.text = rule
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleCardAdapter.ViewHolder {
        val binding = ItemInquiryRuleRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)

    }

    override fun getItemCount() = inquiryViewModel.rules.size

}