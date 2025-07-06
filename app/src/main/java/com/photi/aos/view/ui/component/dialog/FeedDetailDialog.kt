package com.photi.aos.view.ui.component.dialog

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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.model.response.FeedDetailData
import com.photi.aos.data.storage.SharedPreferencesManager
import com.photi.aos.databinding.DialogFeedDetailBinding
import com.photi.aos.view.activity.FeedActivity
import com.photi.aos.view.adapter.CommentsAdapter
import com.photi.aos.view.ui.component.popup.FeedActionPopup
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.FeedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedDetailDialog(val feedId: Int) : DialogFragment(),CustomTwoButtonDialogInterface  {
    private var _binding: DialogFeedDetailBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var mActivity : FeedActivity
    private lateinit var adapter: CommentsAdapter
    private  var myId = SharedPreferencesManager(MyApplication.mySharedPreferences).getUserName() ?: ""
    private var isFirstInput = true
    private var isFirstAdd = true
    private var feedUserName : String? = null


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedDetailBinding.inflate(inflater, container, false)
        mActivity = activity as FeedActivity
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
        val lm = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd  = true
        }
        adapter = CommentsAdapter(feedViewModel,lifecycle,feedId,myId,binding.commentsRecyclerView)
        binding.commentsRecyclerView.adapter = adapter
        binding.commentsRecyclerView.layoutManager = lm
        binding.commentsRecyclerView.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val rv = v as RecyclerView

            // 전체 컨텐츠 높이
            val content = rv.computeVerticalScrollRange()    // API 14+

            // 남는 공간 = RecyclerView 높이 - 컨텐츠 높이
            val empty = rv.height - content

            // top padding을 남는 공간만큼 주면 리스트가 아래로 밀림
            rv.setPadding(
                rv.paddingLeft,
                if (empty > 0) empty else 0,
                rv.paddingRight,
                rv.paddingBottom
            )
        }
        binding.commentsRecyclerView.itemAnimator = null
        binding.commentsRecyclerView.clipToPadding = false

        adapter.addLoadStateListener { state ->
            val refresh = state.source.refresh
            if (refresh is LoadState.NotLoading &&
                feedViewModel.scrollToBottom.value &&           // 내려가야 함?
                adapter.itemCount > 0
            ) {
                binding.commentsRecyclerView.post {
                    lm.scrollToPositionWithOffset(0, 0)     // index 0 = 화면 맨밑
                    feedViewModel.consumeScrollFlag()
                }
            }
        }
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
                                feedViewModel.postComment(feedId, newCommentText, myId)
                                commentEditText.setText("")

                                if (isFirstAdd) {
                                    showCustomToast(removeCommentToastLayout)

                                    isFirstAdd = false
                                }
                            }
                            true
                        } else false
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedViewModel.feedComments.collectLatest { pagingData ->
                    adapter.submitData(pagingData)

                }
            }
        }



        feedViewModel.postCommentResponse.observe(viewLifecycleOwner) { comment ->
            if(comment != null){
                adapter.addComment(comment)

                feedViewModel.consumePostedComment()

                feedViewModel.refreshFeedComments(feedId)

            }
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

    }
    private fun setCustomPopUp(anchor: View) {
        FeedActionPopup(
            context = requireContext(),
            isMyFeed = (feedUserName == myId),
            listener = object : FeedActionPopup.Listener {
                override fun onShare() {
                    Toast.makeText(context, "인스타 공유하기", Toast.LENGTH_SHORT).show()
                }
                override fun onDelete() {
                    CustomTwoButtonDialog(
                        this@FeedDetailDialog,
                        "피드를 삭제할까요?",
                        "삭제한 피드는 복구할 수 없으며,\n오늘 더 이상 피드를 올릴 수 없어요.",
                        "취소할게요",
                        "삭제할게요"
                    ).show(parentFragmentManager, "deleteDialog")
                    dismiss()
                }
                override fun onReport() {
                    feedViewModel.feedId = feedId
                    mActivity.reportFeed()
                }
            }
        ).show(anchor)
    }



    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        feedViewModel.deleteFeed(feedId)
        //listener.onFeedDeleted(index)
    }
}
