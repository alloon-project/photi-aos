package com.example.alloon_aos.view.ui.component.dialog

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.PopupWindow
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.Comment
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.storage.SharedPreferencesManager
import com.example.alloon_aos.databinding.CustomPopupMenuBinding
import com.example.alloon_aos.databinding.DialogFeedDetailBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedDetailDialog(val feedId: Int) : DialogFragment(),CustomTwoButtonDialogInterface  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var adapter:CommentsAdapter
    private var isFirstInput = true
    private var isFirstAdd = true
    private var feedUserName : String? = null
    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        observeLiveData()


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
                    feedUserName = data.username
                    idTextView.text = feedUserName
                    heartCntTextView.text = if (data.likeCnt == 0) "" else data.likeCnt.toString()

                    //TODO 사용자가 heart click 여부
                    // heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)

                    setHeartButtonClickListener(data,binding.heartBtn)

                    Glide.with(feedImgView)
                        .load(data.feedImageUrl)
                        .into(feedImgView)

                    if(data.userImageUrl != ""){
                        Glide.with(profileImageView)
                            .load(data.userImageUrl)
                            .transform(CircleCrop())
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


    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            val fullWidth = displayMetrics.widthPixels
            val fullHeight = displayMetrics.heightPixels

            // dp → px 변환
            val marginHorizontal = (24 * density).toInt() // 좌우 24dp
            val marginVertical = (69 * density).toInt()        // 위 69dp

            // 최종 크기 계산
            val width = fullWidth - 2 * marginHorizontal
            val height = fullHeight - 2 * marginVertical

            // 다이얼로그의 레이아웃 속성 설정
            val params = window.attributes
            params.dimAmount = 0.4f // 배경 어둡기 설정
            window.attributes = params

            // 다이얼로그 크기 설정
            window.setLayout(width, height)

            window.setBackgroundDrawableResource(R.drawable.radius_16)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



//    private fun updateHeartCountView(heartBtn: ImageButton, heartCntTextView: TextView, feed : FeedInItem) {
//        feed.isClick = !feed.isClick
//        heartBtn.setImageResource(if (feed.isClick) R.drawable.ic_heart_filled_14 else R.drawable.ic_heart_empty_14)
//        feed.heartCnt += if (feed.isClick) 1 else -1
//        heartCntTextView.text = if (feed.heartCnt == 0) "" else feed.heartCnt.toString()
//    }
private fun setHeartButtonClickListener(data: FeedDetailData, heartButton: ImageView,) {
    heartButton.setImageResource(
        if (data.isLike) R.drawable.ic_heart_filled_14
        else R.drawable.ic_heart_empty_14
    )

    heartButton.setOnClickListener {
        heartButton.isEnabled = false
        data.isLike = !data.isLike

        val currentLikeText = binding.heartCntTextView.text.toString().replace(",", "")
        val currentCount = currentLikeText.toIntOrNull() ?: 0

        if (data.isLike) {
            feedViewModel.addFeedLike(feedId,
                onComplete =  {
                    heartButton.isEnabled = true
                    heartButton.setImageResource(R.drawable.ic_heart_filled_14)

                    val updatedCount = currentCount + 1
                    binding.heartCntTextView.text = String.format("%,d", updatedCount)
                },
                onFailure = {
                    heartButton.isEnabled = true
                }
            )
        } else {
            feedViewModel.removeFeedLike(
                feedId,
                onComplete = {
                    heartButton.isEnabled = true
                    heartButton.setImageResource(R.drawable.ic_heart_empty_14)

                    val updatedCount = (currentCount - 1).coerceAtLeast(0)
                    binding.heartCntTextView.text = if (updatedCount == 0) {
                        ""
                    } else {
                        String.format("%,d", updatedCount)
                    }
                },
                onFailure = {
                    heartButton.isEnabled = true
                }
            )
        }
    }

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
    private fun setCustomPopUp(anchorView: View) {
        val popupViewBinding = CustomPopupMenuBinding.inflate(LayoutInflater.from(context))
        val popupWindow = PopupWindow(
            popupViewBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 팝업 뷰의 실제 너비 측정
        popupViewBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupViewBinding.root.measuredWidth
        val anchorWidth = anchorView.width

        // 오른쪽 끝을 기준으로 맞추기 위한 x 보정값
        val xOffset = anchorWidth - popupWidth

        // 8dp 아래로 yOffset 보정
        val density = anchorView.resources.displayMetrics.density
        val yOffset = (8 * density).toInt()

        // 위치 설정
        popupWindow.showAsDropDown(anchorView, xOffset, yOffset)
        val myId = sharedPreferencesManager.getUserName() ?: ""
        if (feedUserName == myId) {
            setupMyFeedOptions(popupViewBinding,popupWindow)
        } else {
            setupOtherFeedOptions(popupViewBinding,popupWindow)
        }
    }

    private fun setupMyFeedOptions(
        popupViewBinding: CustomPopupMenuBinding,
        popupWindow: PopupWindow
    ) {
        with(popupViewBinding){
            optionOne.text = "공유하기"
            optionTwo.text = "피드 삭제하기"

            optionOne.setOnClickListener {
                Toast.makeText(context, "인스타 공유하기", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            optionTwo.setOnClickListener {
                CustomTwoButtonDialog(
                    this@FeedDetailDialog,
                    "피드를 삭제할까요?",
                    "삭제한 피드는 복구할 수 없으며,\n오늘 더 이상 피드를 올릴 수 없어요.",
                    "취소할게요",
                    "삭제할게요"
                ).show(requireActivity().supportFragmentManager, "CustomDialog")
                popupWindow.dismiss()
                dismiss()
            }
        }

    }

    private fun setupOtherFeedOptions(popupViewBinding: CustomPopupMenuBinding,  popupWindow: PopupWindow) {
        with(popupViewBinding){
            divider.visibility = View.GONE
            optionTwo.visibility = View.GONE

            optionOne.text = "피드 신고하기"
            optionOne.setOnClickListener {
//TODO 피드 신고하기로 이동
                popupWindow.dismiss()
            }
        }
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
        feedViewModel.deleteFeed(feedId)
        //listener.onFeedDeleted(index)
    }
}