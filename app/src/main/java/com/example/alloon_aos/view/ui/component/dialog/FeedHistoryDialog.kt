package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.data.model.response.FeedHistoryContent
import com.example.alloon_aos.databinding.DialogFeedHistoryBinding
import com.example.alloon_aos.databinding.ItemProofShotsGalleryBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.PhotiViewModel

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

        binding.countTextView.text = count.toString()
        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

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

        photiViewModel.resetPagingParam()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = FeedHistoryAdapter()
        binding.challengeRecyclerview.adapter = adapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.scrollView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            val nestedScrollView = v as NestedScrollView

            // NestedScrollView의 총 높이와 현재 스크롤 위치 확인
            if (scrollY == (nestedScrollView.getChildAt(0).measuredHeight - nestedScrollView.measuredHeight)) {
                val layoutManager = binding.challengeRecyclerview.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!photiViewModel.isLoading && !photiViewModel.isLastPage) {
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        photiViewModel.fetchFeedHistory()
                    }
                }
            }
        }
    }

    private fun observeLiveData() {
        photiViewModel.feedHistoryData.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.toList())
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

    class FeedHistoryAdapter(): ListAdapter<FeedHistoryContent,FeedHistoryAdapter.ViewHolder>(DiffCallback()) {
        inner class ViewHolder(var binding: ItemProofShotsGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data:FeedHistoryContent) {
                Glide.with(binding.challengeImgView.context)
                    .load(data.imageUrl)
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                    .into(binding.challengeImgView)

                binding.chipBtn.text = data.name
                binding.dateTextView.text = data.createdDate.replace("-", ".") + " 인증"

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProofShotsGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class DiffCallback : DiffUtil.ItemCallback<FeedHistoryContent>() {
            override fun areItemsTheSame(
                oldItem: FeedHistoryContent,
                newItem: FeedHistoryContent
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FeedHistoryContent,
                newItem: FeedHistoryContent
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}