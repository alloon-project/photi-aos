package com.example.alloon_aos.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ItemCreateRuleRecyclerviewBinding
import com.example.alloon_aos.databinding.ItemSelectRuleRecyclerviewBinding
import com.example.alloon_aos.viewmodel.CreateViewModel

class CustomRuleAdapter(private val mContext : Context,
                        private val createViewModel: CreateViewModel,
                        private val onItemClickListener: () -> Unit,
                        private val toastListener: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object {
        private const val VIEW_TYPE_ADD = 0
        private const val VIEW_TYPE_RULE = 1
    }

    inner class AddViewHolder(private val binding: ItemCreateRuleRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setListener() {
            binding.root.setOnClickListener {
                if (createViewModel.selectNum < 5)
                    onItemClickListener()
                else
                    toastListener()
            }
        }
    }

    inner class RuleViewHolder(private val binding: ItemSelectRuleRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (createViewModel.customRules[pos]) {
                binding.ruleTextview.setText(rule)
                binding.deleteBtn.visibility = View.VISIBLE

                if (select) {
                    binding.ruleLayout.setBackgroundResource(R.drawable.checkbox_rule_selected)
                    binding.ruleTextview.setTextColor(mContext.getColor(R.color.blue500))
                    binding.deleteBtn.setImageResource(R.drawable.ic_close_circle_blue400)
                } else {
                    binding.ruleLayout.setBackgroundResource(R.drawable.checkbox_rule_default)
                    binding.ruleTextview.setTextColor(mContext.getColor(R.color.gray600))
                    binding.deleteBtn.setImageResource(R.drawable.ic_close_circle_gray100)
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
                    createViewModel.updateCustomRule(pos, this)
                }

                binding.deleteBtn.setOnClickListener {
                    createViewModel.deleteRule(this)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == getItemCount()-1) VIEW_TYPE_ADD else VIEW_TYPE_RULE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_RULE -> {
                val binding = ItemSelectRuleRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RuleViewHolder(binding)
            }
            else -> {
                val binding = ItemCreateRuleRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ADD -> (viewHolder as AddViewHolder).setListener()
            VIEW_TYPE_RULE -> (viewHolder as RuleViewHolder).setContents(position)
        }
    }

    override fun getItemCount() = createViewModel.customRules.size + 1
}