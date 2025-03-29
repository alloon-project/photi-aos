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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.Comment
import com.example.alloon_aos.databinding.CustomPopupMenuBinding
import com.example.alloon_aos.databinding.DialogFeedDetailBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.view.adapter.OnFeedDeletedListener
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedDetailDialog(val feedId: Int, private val listener: OnFeedDeletedListener) : DialogFragment(),CustomTwoButtonDialogInterface  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var adapter:CommentsAdapter
    private var isFirstInput = true
    private var isFirstAdd = true

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        observeLiveData()

        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

        binding.ellipsisImgBtn.setOnClickListener { view ->
            setCustomPopUp(view)
        }

        feedViewModel.fetchChallengeFeedDetail(feedId)
        feedViewModel.fetchFeedComments(feedId)

        return view
    }

    private fun setupRecyclerView() {
        adapter = CommentsAdapter()
        binding.commentsRecyclerView.adapter = adapter
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //TODO 댓글 페이징
//        binding.scrollView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
//            val nestedScrollView = v as NestedScrollView
//
//            // NestedScrollView의 총 높이와 현재 스크롤 위치 확인
//            if (scrollY == (nestedScrollView.getChildAt(0).measuredHeight - nestedScrollView.measuredHeight)) {
//                val layoutManager = binding.challengeRecyclerview.layoutManager as GridLayoutManager
//                val totalItemCount = layoutManager.itemCount
//                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//
//                if (!photiViewModel.isLoading && !photiViewModel.isLastPage) {
//                    if (lastVisibleItemPosition == totalItemCount - 1) {
//                        photiViewModel.fetchFeedHistory()
//                    }
//                }
//            }
//        }
    }


    private fun observeLiveData() {
        feedViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        feedViewModel.challengeFeedDetail.observe(viewLifecycleOwner) { data ->
            with(binding) {
                if (data != null) {
                    idTextView.text = data.username
                    heartCntTextView.text = if (data.likeCnt == 0) "" else data.likeCnt.toString()

                    //TODO 사용자가 heart click 여부
                    // heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)

//                    heartBtn.setOnClickListener {
//                        updateHeartCountView(heartBtn, heartCntTextView, feed)
//                    }

                    Glide.with(feedImgView)
                        .load(data.feedImageUrl)
                        .into(feedImgView)

                    if(data.userImageUrl != ""){
                        Glide.with(profileImageView)
                            .load(data.userImageUrl)
                            .transform(CenterCrop())
                            .into(profileImageView)
                    }

                    commentEditText.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {}

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (s == null)
                                textLengthTextView.text = "0/16"
                            else {
                                textLengthTextView.text = s.length.toString() + "/16"

                                if (isFirstInput) {
                                    showCustomToast(addCommentToastLayout)
                                    isFirstInput = false
                                }

                            }

                        }
                    })

                    commentEditText.setOnEditorActionListener { view, i, event ->
                        if (i == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                            val newCommentText = commentEditText.text.toString()

                            if (newCommentText.isNotEmpty()) {
                                addComment(newCommentText)
                                commentEditText.setText("")

                                if (isFirstAdd) {
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
            }
        }

        feedViewModel.feedComments.observe(viewLifecycleOwner){
            data ->
            adapter.submitList(data?.toList() ?: emptyList())
        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인 되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생 했습니다."
        )

        if (code == "200 OK") {
            return
        }

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("FeedCountDialog", "Error: $message")
        }
    }

    private fun addComment(commentText: String, ) {
        //댓글 추가 api
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

//    private fun updateHeartCountView(heartBtn: ImageButton, heartCntTextView: TextView, feed : FeedInItem) {
//        feed.isClick = !feed.isClick
//        heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
//        feed.heartCnt += if (feed.isClick) 1 else -1
//        heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
//    }

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
            //if(본인이 아니면)
                //optionOne.text = 피드 신고하기
            //else
                optionOne.text = "인스타 공유하기"
                optionTwo.text = "피드 삭제하기"

            optionOne.setOnClickListener {
                //if(본인이 아니면)
                    //피드 신고 플로우
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

    class CommentsAdapter: ListAdapter<Comment, CommentsAdapter.ViewHolder>(
        DiffCallback()
    ) {
        inner class ViewHolder(var binding: ItemFeedCommentBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data : Comment) {
                with(data) {
                    binding.idTextView.text = username
                    binding.commentTextView.text = comment

                    //TODO 내 인증인 경우만 삭제되게
                    binding.commentLayout.setOnLongClickListener {
                        //댓글 삭제 api
                        true
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemFeedCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder,  position: Int) {
            holder.bind(getItem(position))
        }

        class DiffCallback : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        //TODO 삭제
        //listener.onFeedDeleted(index)
    }
}