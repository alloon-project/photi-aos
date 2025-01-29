package com.example.alloon_aos.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.data.model.response.FeedContent
import com.example.alloon_aos.databinding.ItemFeedDefaultOutBinding
import com.example.alloon_aos.viewmodel.FeedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class FeedOutAdapter(private val fragmentManager: FragmentManager, val feedViewModel: FeedViewModel):
    ListAdapter<FeedContent, FeedOutAdapter.ViewHolder>(DiffCallback())  {
    inner class ViewHolder(var binding : ItemFeedDefaultOutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: FeedContent) {
            binding.dateTextView.text = getRelativeDate(data.createdDate)

            //TODO : today이면 progress bar 용 총 피드 멤버 수 저장 data.feedMemberCnt

            val feedInAdapter =  FeedInAdapter(fragmentManager)
            binding.feedInRecyclerView.adapter = feedInAdapter
            binding.feedInRecyclerView.layoutManager = GridLayoutManager(binding.root.context, 2)
            binding.feedInRecyclerView.setHasFixedSize(true)

        }
    }

    fun getRelativeDate(createdDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(createdDate, formatter)
        val today = LocalDate.now()

        val daysBetween = ChronoUnit.DAYS.between(date, today)

        return when (daysBetween) {
            0L -> "오늘"
            else -> "${daysBetween}일 전"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedOutAdapter.ViewHolder {
        var view = ItemFeedDefaultOutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedOutAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedContent>() {
        override fun areItemsTheSame(
            oldItem: FeedContent,
            newItem: FeedContent
        ): Boolean {
            return oldItem.feeds == newItem.feeds
        }

        override fun areContentsTheSame(
            oldItem: FeedContent,
            newItem: FeedContent
        ): Boolean {
            return oldItem == newItem
        }
    }
}