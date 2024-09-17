package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.DialogFeedDetailBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.viewmodel.Comment
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedDetailDialog(val index: Int) : DialogFragment()  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val feed = feedViewModel.feedInItems[index]

        with(binding) {
            idTextView.text = feed.id
            heartCntTextView.text = feed.heartCnt.toString()

            if(feed.isClick)
                heartBtn.setImageResource(R.drawable.ic_heart_filled)


            Glide.with(root)
                .load(feed.url)
                .into(feedImgView)

            commentsRecyclerView.adapter = CommentsAdapter(feed.comments)
            commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            commentsRecyclerView.setHasFixedSize(true)

            backImgBtn.setOnClickListener {
                dismiss()
            }

            ellipsisImgBtn.setOnClickListener {
                // 공유하기, 삭제하기
            }

            commentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null)
                        textLengthTextView.text = "0/16"
                    else
                        textLengthTextView.text = s.length.toString()+"/16"
                }
            })
        }

        return view
    }



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        setBlurredBackground(50, 0)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setBlurredBackground(blurBehindRadius: Int, backgroundBlurRadius: Int) {
        val dialogWindow = dialog?.window
        dialogWindow?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogWindow?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        if (dialogWindow?.windowManager?.isCrossWindowBlurEnabled == true) {
            dialogWindow.setFlags(
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            )
            dialogWindow.attributes?.blurBehindRadius = blurBehindRadius
            dialogWindow.setDimAmount(0f)
        }
    }

    class CommentsAdapter(val comments: ArrayList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
        inner class ViewHolder(var binding: ItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setContents(holder: ViewHolder, pos: Int) {
                with(comments[pos]) {
                    println(id)
                    binding.idTextView.text = id
                    binding.textTextView.text = text
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemFeedCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder, position)
        }

        override fun getItemCount() = comments.size
    }

}