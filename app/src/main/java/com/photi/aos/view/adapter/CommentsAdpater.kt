package com.photi.aos.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photi.aos.data.model.response.Comment
import com.photi.aos.databinding.ItemFeedCommentBinding
import com.photi.aos.viewmodel.FeedViewModel

class CommentsAdapter(val feedViewModel: FeedViewModel, private val lifecycle: Lifecycle,
                      val feedId : Int, val myId : String, val recyclerView: RecyclerView
)
    : PagingDataAdapter<Comment, CommentsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment) {
            with(binding) {
                idTextView.text = data.username
                commentTextView.text = data.comment

                if(myId == data.username){
                    commentLayout.setOnLongClickListener {
                        feedViewModel.deleteComment(feedId = feedId , commentId =  data.id.toInt(), onComplete = {
                            removeCommentById(commentId = data.id.toInt())
                        }, onFailure = {
                        })
                        true
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
    fun addComment(comment: Comment) {
        val current = snapshot().items.toMutableList()  // 현재까지 로드된 아이템
        current.add(0,comment)                           // 맨 앞에 삽입
        submitData(lifecycle, PagingData.from(current))

        recyclerView.post {
            (recyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }
    }

    fun removeCommentById(commentId: Int) {
        val current = snapshot().items.toMutableList()
        val idx = current.indexOfFirst { it.id.toInt() == commentId }
        if (idx != -1) {
            current.removeAt(idx)
            submitData(lifecycle, PagingData.from(current))
        }
        recyclerView.post {
            (recyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }

    }
    class DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}
