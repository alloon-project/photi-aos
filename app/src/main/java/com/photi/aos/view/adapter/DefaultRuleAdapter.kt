package com.photi.aos.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photi.aos.R
import com.photi.aos.databinding.ItemSelectRuleRecyclerviewBinding
import com.photi.aos.viewmodel.CreateViewModel

class DefaultRuleAdapter(private val mContext : Context,
                         private val createViewModel: CreateViewModel,
                         private val toastListener: () -> Unit) :
    RecyclerView.Adapter<DefaultRuleAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSelectRuleRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (createViewModel.defaultRules[pos]) {
                binding.ruleTextview.text = rule

                if (select) {
                    binding.ruleLayout.setBackgroundResource(R.drawable.checkbox_rule_selected)
                    binding.ruleTextview.setTextColor(mContext.getColor(R.color.blue500))
                } else {
                    binding.ruleLayout.setBackgroundResource(R.drawable.checkbox_rule_default)
                    binding.ruleTextview.setTextColor(mContext.getColor(R.color.gray600))
                }

                binding.root.setOnClickListener {
                    if (!select) {
                        if (createViewModel.selectNum < 5) {
                            select = true
                            createViewModel.selectNum++
                        } else {
                            toastListener()
                        }
                    } else {
                        select = false
                        createViewModel.selectNum--
                    }
                    createViewModel.updateDefaultRule(pos, this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultRuleAdapter.ViewHolder {
        val binding = ItemSelectRuleRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContents(position)

    }

    override fun getItemCount() = 6

}