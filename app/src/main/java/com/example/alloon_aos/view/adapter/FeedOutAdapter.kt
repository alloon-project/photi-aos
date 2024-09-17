package com.example.alloon_aos.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.ItemFeedDefaultOutBinding
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedOutAdapter(private val fragmentManager: FragmentManager, val feedViewModel: FeedViewModel): RecyclerView.Adapter<FeedOutAdapter.ViewHolder>() {
    inner class ViewHolder(var binding : ItemFeedDefaultOutBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (feedViewModel.feedOutItems[pos]) {
                if(daysAgo == 0){
                    binding.timeLayout.visibility = View.VISIBLE
                }
                else{
                    binding.dateTextView.text = daysAgo.toString()+ "일 전"
                }

                binding.feedInRecyclerView.adapter = FeedInAdapter(fragmentManager,feedInItems)
                binding.feedInRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 2)
                binding.feedInRecyclerView.setHasFixedSize(true)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedOutAdapter.ViewHolder {
        var view = ItemFeedDefaultOutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: FeedOutAdapter.ViewHolder, position: Int) {
        viewHolder.setContents(viewHolder,position)
    }

    override fun getItemCount() = feedViewModel.feedOutItems.size

}