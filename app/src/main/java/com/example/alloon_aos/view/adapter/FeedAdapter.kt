package com.example.alloon_aos.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.Feed
import com.example.alloon_aos.data.model.response.FeedUiItem
import com.example.alloon_aos.databinding.ItemFeedContentBinding
import com.example.alloon_aos.databinding.ItemFeedHeaderBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.FeedViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.Duration


class FeedAdapter(
    private val fragmentManager: FragmentManager,
    private val feedViewModel: FeedViewModel,
    private val lifecycle: Lifecycle,
) : PagingDataAdapter<FeedUiItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        const val VIEW_TYPE_HEADER = 0
         const val VIEW_TYPE_FEED = 1
    }

    inner class HeaderViewHolder(private val binding: ItemFeedHeaderBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(headerData: FeedUiItem.Header) {
            if(headerData.dateLabel.equals("오늘")){
                if (feedViewModel.isUserVerifiedToday.value == true) {
                     binding.proveLayout.visibility = View.VISIBLE
                }else{
                    binding.timeLayout.visibility = View.VISIBLE
                    val proveTime = feedViewModel.challengeInfo.value?.proveTime
                    binding.proveTimeTextView.text = proveTime?.let { "${it}까지" } ?: ""
                }
            }


            binding.dateTextView.text = headerData.dateLabel
        }
    }

    inner class FeedViewHolder(private val binding: ItemFeedContentBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Feed) {
            binding.timeTextView.text = formatTimeAgo(data.createdDateTime)
            binding.idTextView.text = data.username

            Glide.with(binding.imgView.context)
                .load(data.imageUrl)
                .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                .into(binding.imgView)

            setHeartButtonClickListener(data, binding.heartButton, binding.proofshotShape)

            binding.feedLayout.setOnClickListener {
                val dialog = FeedDetailDialog(feedId = data.id)
                dialog.show(fragmentManager, "FeedDetailDialog")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemFeedHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemFeedContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FeedViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is FeedUiItem.Header -> {
                (holder as? HeaderViewHolder)?.bind(item)
            }
            is FeedUiItem.Content -> {
                (holder as? FeedViewHolder)?.bind(item.feed)
            }
            null -> { /* do nothing */ }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FeedUiItem.Header -> VIEW_TYPE_HEADER
            is FeedUiItem.Content -> VIEW_TYPE_FEED
            null -> VIEW_TYPE_FEED
        }
    }

    fun removeFeedById(feedId: Int) {
        val currentList = snapshot().items.toMutableList()

        // 삭제할 피드 위치
        val feedIndex = currentList.indexOfFirst {
            it is FeedUiItem.Content && it.feed.id == feedId
        }

        if (feedIndex == -1) return

        // 삭제할 피드 바로 앞의 헤더 찾기
        val headerIndex = (feedIndex downTo 0).firstOrNull {
            currentList[it] is FeedUiItem.Header
        }

        // 피드 삭제
        currentList.removeAt(feedIndex)

        // 헤더 아래에 해당 헤더의 날짜와 일치하는 피드가 더 남아있는지 확인
        val header = headerIndex?.let { currentList.getOrNull(it) as? FeedUiItem.Header }

        val hasFeedUnderHeader = header != null && currentList.any {
            it is FeedUiItem.Content &&
                    formatHeader(it.feed.createdDateTime) == header.dateLabel
        }

        if (!hasFeedUnderHeader && headerIndex != null) {
            currentList.removeAt(headerIndex)
        }

        submitData(lifecycle, PagingData.from(currentList))
    }

    private fun formatHeader(dateString: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))
        val inputDate = Instant.from(formatter.parse(dateString))

        val now = Instant.now()
        val duration = Duration.between(inputDate, now)

        return when {
            duration.toDays() <= 1 -> "오늘"
            else -> "${duration.toDays()}일전"
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<FeedUiItem>() {
        override fun areItemsTheSame(oldItem: FeedUiItem, newItem: FeedUiItem): Boolean {
            // Header vs Header / Content vs Content 에 따라 비교
            return when {
                oldItem is FeedUiItem.Header && newItem is FeedUiItem.Header ->
                    oldItem.dateLabel == newItem.dateLabel
                oldItem is FeedUiItem.Content && newItem is FeedUiItem.Content ->
                    oldItem.feed.id == newItem.feed.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: FeedUiItem, newItem: FeedUiItem): Boolean {
            return oldItem == newItem
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

    private fun setHeartButtonClickListener(data: Feed, heartButton: ImageView, clickArea : ImageView) {
        heartButton.setImageResource(
            if (data.isLike) R.drawable.ic_heart_filled_14
            else R.drawable.ic_heart_empty_14
        )

        clickArea.setOnClickListener {
            clickArea.isEnabled = false
            data.isLike = !data.isLike

            if (data.isLike) {
                feedViewModel.addFeedLike(
                    data.id,
                    onComplete = {
                        clickArea.isEnabled = true
                        heartButton.setImageResource(R.drawable.ic_heart_filled_14)
                    },
                    onFailure = {
                        clickArea.isEnabled = true

                    }
                )
            } else {
                feedViewModel.removeFeedLike(
                    data.id,
                    onComplete =  {
                        clickArea.isEnabled = true
                        heartButton.setImageResource(R.drawable.ic_heart_empty_14)
                    },
                    onFailure = {
                        clickArea.isEnabled = true
                    }
                )
            }
        }
    }


}
