package com.photi.aos.view.ui.component.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.photi.aos.R
import com.photi.aos.data.model.response.FeedHistoryContent
import com.photi.aos.databinding.DialogFeedHistoryBinding
import com.photi.aos.databinding.ItemProofShotsGalleryBinding
import com.photi.aos.view.activity.FeedActivity
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.InstagramStory
import com.photi.aos.view.ui.util.LoadingDialogManager
import com.photi.aos.view.ui.util.RoundedCornersTransformation
import com.photi.aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class FeedHistoryDialog : DialogFragment() {

    companion object {
        private const val KEY_COUNT = "key_count"

        fun newInstance(count: Int) = FeedHistoryDialog().apply {
            arguments = bundleOf(KEY_COUNT to count)
        }
    }

    private val count: Int by lazy { requireArguments().getInt(KEY_COUNT) }

    private var _binding: DialogFeedHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:FeedHistoryAdapter
    private val photiViewModel by activityViewModels<PhotiViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFeedHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        observeLiveData()
        binding.backImgBtn.setOnClickListener{
            dismiss()
        }
        binding.countTextView.text = count.toString()

        photiViewModel.fetchFeedHistory()
        return view
    }

    override fun onResume() {
        super.onResume()

        // 전체화면 설정
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photiViewModel.clearFeedHistoryData()
       // photiViewModel.resetPagingParam()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = FeedHistoryAdapter(
            requireActivity(),
            onShareClick = { imageUrl, challengeName ->
                if (imageUrl.isBlank()) {
                    CustomToast.createToast(requireActivity(), "공유할 이미지가 없어요.", "circle")?.show()
                    return@FeedHistoryAdapter
                }

                shareToInstagram(imageUrl, challengeName, getString(R.string.facebook_app_id))
            }
        )
        binding.challengeRecyclerview.adapter = adapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
    }


    private fun setLoading(loading: Boolean) {
        if (loading)
            LoadingDialogManager.show(requireActivity())
        else
            LoadingDialogManager.hide()
    }

    fun shareToInstagram(feedBgImg: String, challengeTitle: String, appId: String) {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                InstagramStory.shareSingleImageBgAndStickerUrl(
                    activity = requireActivity(),
                    context = requireContext(),
                    bgResId = R.drawable.ig_bg,
                    stickerImageUrl = feedBgImg,
                    challengeTitle = challengeTitle,
                    sourceAppId = appId,
                    stickerBottomOffsetPx = null,
                    cornerDp = 16f,
                    circle = false,
                    borderPx = 16,
                    overlayResId = R.drawable.graphic_photi_logo_white,
                    overlayWidthPx = 62,
                    overlayHeightPx = 12,
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                CustomToast.createToast(activity, "공유에 실패했어요. 다시 시도해주세요.", "circle")?.show()

            } finally {
                setLoading(false)
            }
        }
    }


    private fun observeLiveData() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photiViewModel.feedHistoryData.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        photiViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }
    }

    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "TOKEN_UNAUTHENTICATED" to "승인 되지 않은 요청입니다. 다시 로그인 해주세요.",
            "EXPIRED_TOKEN" to "만료된 토큰입니다.",
            "INVALID_TOKEN" to "유효하지 않은 토큰입니다.",
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


    class FeedHistoryAdapter(private val activity :FragmentActivity,   private val onShareClick: (imageUrl: String, challengeName: String) -> Unit) : PagingDataAdapter<FeedHistoryContent, FeedHistoryAdapter.ViewHolder>(DiffCallback()) {

        inner class ViewHolder(val binding: ItemProofShotsGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: FeedHistoryContent) {
                Glide.with(binding.challengeImgView.context)
                    .load(data.imageUrl)
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 76f))
                    .into(binding.challengeImgView)

                binding.chipBtn.text = data.name
                binding.dateTextView.text = data.createdDate.replace("-", ".") + " 인증"

               if(data.isDeleted){
                   bindDeletedChallengeUI(binding)
               }else{
                   bindActiveChallengeUI(binding, data)
               }

            }
        }

        private fun bindDeletedChallengeUI(binding: ItemProofShotsGalleryBinding) {
            binding.challengeImgView.setOnClickListener {
                CustomToast.createToast(activity, "탈퇴한 챌린지예요.", "close")?.show()
            }

            binding.shortcutImgBtn.apply {
                visibility = View.GONE
                setOnClickListener(null)
            }
        }

        private fun bindActiveChallengeUI(binding: ItemProofShotsGalleryBinding, data: FeedHistoryContent) {
            val ctx = binding.root.context

            binding.challengeImgView.setOnClickListener {
                val intent = Intent(ctx, FeedActivity::class.java).apply {
                    putExtra("CHALLENGE_ID", data.challengeId)
                    putExtra("FEED_ID", data.feedId)
                }
                ctx.startActivity(intent)
            }

            binding.shortcutImgBtn.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    onShareClick(data.imageUrl, data.name)
                }
            }
        }



        private fun sendInviteMsg() {
           // TODO invite code 추가 시
//            val inviteCode = feedViewModel.invitecode
//            val appLink = "https://photi.com/challenge/$inviteCode"
//            val chooserTitle = "소설 필사하기"
//            var message = ""
//
//            if (!feedChallengeData.isPublic)
//                message = "[Photi] ‘$chooserTitle' 챌린지에 함께 참여해 보세요!\n* 초대코드 : $inviteCode \n\n$appLink"
//            else
//                message = "[Photi] ‘$chooserTitle' 챌린지에 함께 참여해 보세요!\n* \n\n$appLink"
//
//            val sendIntent = Intent(Intent.ACTION_SEND)
//            sendIntent.type = "text/plain"
//            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
//
//            val chooser = Intent.createChooser(sendIntent, chooserTitle)
//            startActivity(chooser)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProofShotsGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }

        class DiffCallback : DiffUtil.ItemCallback<FeedHistoryContent>() {
            override fun areItemsTheSame(oldItem: FeedHistoryContent, newItem: FeedHistoryContent): Boolean {
                return oldItem.feedId == newItem.feedId
            }

            override fun areContentsTheSame(oldItem: FeedHistoryContent, newItem: FeedHistoryContent): Boolean {
                return oldItem == newItem
            }
        }
    }

}