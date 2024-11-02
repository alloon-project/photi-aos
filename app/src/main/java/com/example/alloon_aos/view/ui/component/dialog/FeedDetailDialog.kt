package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.CustomPopupMenuBinding
import com.example.alloon_aos.databinding.DialogFeedDetailBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.view.adapter.OnFeedDeletedListener
import com.example.alloon_aos.viewmodel.Comment
import com.example.alloon_aos.viewmodel.FeedInItem
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedDetailDialog(val index: Int,private val listener: OnFeedDeletedListener) : DialogFragment(),CustomTwoButtonDialogInterface  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var isFirstInput = true
    private var isFirstAdd = true

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

            ellipsisImgBtn.setOnClickListener { view ->
                setCustomPopUp(view)
            }

            heartBtn.setOnClickListener {
                updateHeartCountView(heartBtn, heartCntTextView, feed)
            }


            commentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null)
                        textLengthTextView.text = "0/16"
                    else{
                        textLengthTextView.text = s.length.toString()+"/16"

                        if(isFirstInput){
                            showCustomToast(addCommentToastLayout)
                            isFirstInput = false
                        }

                    }

                }
            })

            commentEditText.setOnEditorActionListener { view, i, event ->
                if(i == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    val newCommentText = commentEditText.text.toString()

                    if(newCommentText.isNotEmpty()){
                        addComment(feed,newCommentText,commentsRecyclerView)
                        commentEditText.setText("")

                        if(isFirstAdd){
//                            commentsRecyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
//                                override fun onLayoutChange(
//                                    v: View?,
//                                    left: Int,
//                                    top: Int,
//                                    right: Int,
//                                    bottom: Int,
//                                    oldLeft: Int,
//                                    oldTop: Int,
//                                    oldRight: Int,
//                                    oldBottom: Int
//                                ) {
//                                    // 레이아웃이 변경된 후에 토스트를 띄움
                                    showCustomToast(removeCommentToastLayout)
//
//                                    // 리스너를 한 번만 적용하고 제거
//                                    commentsRecyclerView.removeOnLayoutChangeListener(this)
//                                }
//                            })

                            isFirstAdd = false
                        }
                    }
                    true
                } else false
            }
        }

        return view
    }

    private fun addComment(feed: FeedInItem, commentText: String, recyclerView: RecyclerView) {
        val newComment = Comment(id = feedViewModel.id, text = commentText)
        feed.comments.add(newComment)
        recyclerView.adapter?.notifyItemInserted(feed.comments.size - 1)
        recyclerView.scrollToPosition(feed.comments.size - 1)
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

    private fun updateHeartCountView(heartBtn: ImageButton, heartCntTextView: TextView, feed : FeedInItem) {
        feed.isClick = !feed.isClick
        heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
        feed.heartCnt += if (feed.isClick) 1 else -1
        heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
    }

    private fun showCustomToast(customToastLayout: ConstraintLayout) {
//        // 마지막 아이템의 뷰를 찾아서 그 뷰를 기준으로 토스트 위치를 설정
//        val lastVisiblePosition = recyclerView.adapter?.itemCount?.minus(1) ?: return@addOnGlobalLayoutListener
//        val lastVisibleItemView = recyclerView.findViewHolderForAdapterPosition(lastVisiblePosition)?.itemView
//
//        if (lastVisibleItemView != null) {
//            // ConstraintSet을 사용하여 토스트 레이아웃의 제약 조건을 변경
//            val constraintLayout = customToastLayout.parent as ConstraintLayout
//            val constraintSet = ConstraintSet()
//            constraintSet.clone(constraintLayout)
//
//            // customToastLayout의 BOTTOM을 새로 추가된 마지막 아이템의 TOP에 맞추기
//            constraintSet.connect(
//                customToastLayout.id, // 토스트 레이아웃
//                ConstraintSet.BOTTOM, // 토스트 레이아웃의 BOTTOM
//                lastVisibleItemView.id, // 마지막 아이템의 ID
//                ConstraintSet.TOP // 마지막 아이템의 TOP에 맞추기
//            )
//
//            // 수정된 제약 조건을 적용
//            constraintSet.applyTo(constraintLayout)

            // 애니메이션 처리
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                duration = 300
                fillAfter = true
            }

            val fadeOut = AlphaAnimation(1f, 0f).apply {
                startOffset = 700 // 0.7초 대기 후 사라지기 시작
                duration = 300
                fillAfter = true
            }

            customToastLayout.visibility = View.VISIBLE
            customToastLayout.startAnimation(fadeIn)

            Handler(Looper.getMainLooper()).postDelayed({
                customToastLayout.startAnimation(fadeOut)
                customToastLayout.visibility = View.GONE
            }, 1000)
//        } else {
//            Log.e("showCustomToast", "RecyclerView의 마지막 아이템을 찾을 수 없습니다.")
//        }
    }


    private fun setCustomPopUp(view: View) {
        val popupViewBinding = CustomPopupMenuBinding.inflate(LayoutInflater.from(context))
        val popupWindow = PopupWindow(popupViewBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        with(popupViewBinding){
            optionOne.text = "인스타 공유하기"
            optionTwo.text = "피드 삭제하기"

            optionOne.setOnClickListener {
                Toast.makeText(context, "Share 클릭됨", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            optionTwo.setOnClickListener {
                CustomTwoButtonDialog(this@FeedDetailDialog,
                    "피드를 삭제할까요?","삭제한 피드는 복구할 수 없으며,\n" + "오늘 더 이상 피드를 올릴 수 없어요.",
                    "취소할게요","삭제할게요")
                .show(requireActivity().supportFragmentManager, "CustomDialog")
                popupWindow.dismiss()
                dismiss()
            }
        }

        popupWindow.showAsDropDown(view, 0, 0, Gravity.CENTER)
    }

    class CommentsAdapter(val comments: ArrayList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
        inner class ViewHolder(var binding: ItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setContents(holder: ViewHolder, pos: Int) {
                with(comments[pos]) {
                    binding.textView.text = id
                    binding.idTextView.text = text

                    holder.itemView.setOnLongClickListener {
                        removeComment(pos)
                        true
                    }
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

        private fun removeComment(position: Int) {
            comments.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, comments.size)
        }
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        listener.onFeedDeleted(index)
    }
}