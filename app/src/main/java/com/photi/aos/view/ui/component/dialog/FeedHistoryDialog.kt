package com.photi.aos.view.ui.component.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photi.aos.data.model.response.FeedHistoryContent
import com.photi.aos.databinding.DialogFeedHistoryBinding
import com.photi.aos.databinding.ItemProofShotsGalleryBinding
import com.photi.aos.view.activity.FeedActivity
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedHistoryDialog(val count : Int): DialogFragment() {
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
        adapter = FeedHistoryAdapter()
        binding.challengeRecyclerview.adapter = adapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
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


    class FeedHistoryAdapter : PagingDataAdapter<FeedHistoryContent, FeedHistoryAdapter.ViewHolder>(DiffCallback()) {

        inner class ViewHolder(val binding: ItemProofShotsGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: FeedHistoryContent) {
                Glide.with(binding.challengeImgView.context)
                    .load(data.imageUrl)
                    .into(binding.challengeImgView)
                binding.chipBtn.text = data.name
                binding.dateTextView.text = data.createdDate.replace("-", ".") + " 인증"

                binding.challengeImgView.setOnClickListener {
                    val ctx = binding.root.context
                    val intent = Intent(ctx, FeedActivity::class.java).apply {
                        putExtra("CHALLENGE_ID", data.challengeId)
                        putExtra("FEED_ID", data.feedId)
                    }
                    ctx.startActivity(intent)
                }

                binding.shortcutImgBtn.setOnClickListener{
                    sendInviteMsg()
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