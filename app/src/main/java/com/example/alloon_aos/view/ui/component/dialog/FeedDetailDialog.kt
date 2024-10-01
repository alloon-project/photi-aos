package com.example.alloon_aos.view.ui.component.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.DialogFeedDetailBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.dpToPx
import com.example.alloon_aos.viewmodel.Comment
import com.example.alloon_aos.viewmodel.FeedInItem
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedDetailDialog(val index: Int) : DialogFragment()  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var isFirstInput = true

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val feed = feedViewModel.feedInItems[index]

        with(binding) {
            idTextView.text = feed.id
            heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
            heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)

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

            heartBtn.setOnClickListener {
                updateHeartCountView(heartBtn, heartCntTextView, feed)
            }


            //글자 수 바뀔 때
            commentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null)
                        textLengthTextView.text = "0/16"
                    else{
                        textLengthTextView.text = s.length.toString()+"/16"

                        if(isFirstInput){
                            showToastAbove("엔터를 누르면 댓글이 전송돼요~")
                            isFirstInput = false
                        }

                    }

                }
            })

            commentEditText.setOnEditorActionListener { view, i, event ->
                if(i == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    val newCommentText = commentEditText.text.toString()

                    if(newCommentText.isNotEmpty()){
                        //사용자 id 넣어줘야함
                        val newComment = Comment(id = "myID", text = newCommentText)
                        feed.comments.add(newComment)

                        commentsRecyclerView.adapter?.notifyItemInserted(feed.comments.size - 1)
                        commentsRecyclerView.scrollToPosition(feed.comments.size - 1)

                        Toast.makeText(requireContext(), "댓글 전송~", Toast.LENGTH_SHORT).show()
                        commentEditText.setText("")
                    }
                    true
                } else {
                    false
                }
            }
        }

        return view
    }


    private fun updateHeartCountView(heartBtn: ImageButton, heartCntTextView: TextView, feed : FeedInItem) {
        feed.isClick = !feed.isClick
        heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
        feed.heartCnt += if (feed.isClick) 1 else -1
        heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
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

    //아직 토스트 메시지의 위치 값 넣지 않음.
    private fun showToastAbove(message:String){
        val inflater = LayoutInflater.from(requireContext())
        val customToastView = inflater.inflate(R.layout.toast_tooltip_right, null)

        customToastView.findViewById<TextView>(R.id.textView).text = message

        val toast = Toast(requireContext())
        val yOffset = requireContext().dpToPx(470f)

        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0,yOffset )
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastView
        toast.show()
    }

}