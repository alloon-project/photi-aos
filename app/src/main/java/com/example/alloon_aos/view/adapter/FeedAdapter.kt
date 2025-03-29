package com.example.alloon_aos.view.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.Feed
import com.example.alloon_aos.databinding.ItemFeedOutBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.FeedInItem
import com.example.alloon_aos.viewmodel.FeedViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.ZonedDateTime
interface OnFeedDeletedListener {
    fun onFeedDeleted(position: Int)
}

class FeedAdapter(private val fragmentManager: FragmentManager, val feedViewModel: FeedViewModel):
    PagingDataAdapter<Feed, FeedAdapter.ViewHolder>(DiffCallback()), OnFeedDeletedListener  {
    inner class ViewHolder(var binding : ItemFeedOutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Feed) {
            binding.timeTextView.text = formatTimeAgo(data.createdDateTime)
            binding.idTextView.text = data.username
            Glide
                .with(binding.imgView.context)
                .load(data.imageUrl)
                .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                .into(binding.imgView)
//            //TODO : 하트 클릭, 클릭 시 다이얼로그+ondelete


            binding.heartButton.setImageResource(if (data.isLike) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
            binding.heartButton.setOnClickListener {
                binding.heartButton.isEnabled = false

                data.isLike = !data.isLike

                if (data.isLike) {
                    feedViewModel.addFeedLike(data.id) {
                        binding.heartButton.isEnabled = true
                        binding.heartButton.setImageResource(R.drawable.ic_heart_filled_14)
                    }
                } else {
                    feedViewModel.removeFeedLike(data.id) {
                        binding.heartButton.isEnabled = true
                        binding.heartButton.setImageResource(R.drawable.ic_heart_empty_14)
                    }
                }
            }

//            binding.feed.setOnClickListener {
//                FeedDetailDialog(data.id,this@FeedInAdapter)
//                    .show(fragmentManager,"FeedDetailDialog")
//            }

        }
    }

   private fun formatTimeAgo(dateString: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))
        val inputDate = Instant.from(formatter.parse(dateString))

        val now = Instant.now()
        val duration = Duration.between(inputDate, now)

        Log.d("formatTime", "$now ${duration.toMinutes()} $inputDate")

        return when {
            duration.toMinutes() < 1 -> "방금"
            duration.toHours() < 1 -> "${duration.toMinutes()}분전"
            duration.toDays() < 1 -> "${duration.toHours()}시간전"
            else -> "${duration.toDays()}일전"
        }
    }

    private fun updateHeartIcon(heartBtn: ImageButton, heartCntTextView: TextView, feed : FeedInItem) {
        feed.isClick = !feed.isClick
        heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
        feed.heartCnt += if (feed.isClick) 1 else -1
        heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.ViewHolder {
        var view = ItemFeedOutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class DiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onFeedDeleted(position: Int) {
//        val currentList = currentList.toMutableList() // 현재 리스트를 변경 가능한 MutableList로 변환
//        currentList.removeAt(position) // 해당 위치의 아이템 삭제
//        submitList(currentList) // 새로운 리스트를 적용 (DiffUtil 자동 반영)
    }
}