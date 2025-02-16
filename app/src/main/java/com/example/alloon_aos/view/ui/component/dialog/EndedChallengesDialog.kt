package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.alloon_aos.data.model.response.EndedChallengeContent
import com.example.alloon_aos.data.model.response.MemberImage
import com.example.alloon_aos.databinding.DialogEndedChallengesBinding
import com.example.alloon_aos.databinding.ItemEndedChallengesBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EndedChallengesDialog(val count: Int) : DialogFragment() {
    private var _binding: DialogEndedChallengesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EndedChallengesAdpater
    private val photiViewModel by activityViewModels<PhotiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEndedChallengesBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()
        observeLiveData()

        photiViewModel.fetchEndedChallenges()

        binding.countTextView.text = count.toString()
        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photiViewModel.clearEndedChallengeData()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = EndedChallengesAdpater()
        binding.challengeRecyclerview.adapter = adapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

    }

    private fun observeLiveData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photiViewModel.endedChallengeData.collectLatest { pagingData ->
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
            Log.e("EndedChallengesDialog", "Error: $message")
        }
    }

    class EndedChallengesAdpater :
        PagingDataAdapter<EndedChallengeContent, EndedChallengesAdpater.ViewHolder>(DiffCallback()) {

        inner class ViewHolder(private val binding: ItemEndedChallengesBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(data: EndedChallengeContent) {
                binding.titleTextView.text = data.name
                binding.dateTextView.text = data.endDate.replace("-", ".") + " 종료"
                Glide.with(binding.imgView.context)
                    .load(data.imageUrl)
                    .into(binding.imgView)

                setupAvatar(data.currentMemberCnt, data.memberImages, binding)

            }
        }

        private fun setupAvatar(
            currentMemberCnt: Int,
            memberImages: List<MemberImage>,
            binding: ItemEndedChallengesBinding
        ) {
            when (currentMemberCnt) {
                1 -> {
                    binding.avatarOneLayout.visibility = View.VISIBLE
                    loadImage(binding.oneUser1ImageView, memberImages.getOrNull(0)?.memberImage)
                }
                2 -> {
                    binding.avatarTwoLayout.visibility = View.VISIBLE
                    loadImage(binding.twoUser1ImageView, memberImages.getOrNull(0)?.memberImage)
                    loadImage(binding.twoUser2ImageView, memberImages.getOrNull(1)?.memberImage)
                }
                3 -> {
                    binding.avatarThreeLayout.visibility = View.VISIBLE
                    loadImage(binding.threeUser1ImageView, memberImages.getOrNull(0)?.memberImage)
                    loadImage(binding.threeUser2ImageView, memberImages.getOrNull(1)?.memberImage)
                    loadImage(binding.threeUser3ImageView, memberImages.getOrNull(2)?.memberImage)
                }
                else -> {
                    binding.avatarMultipleLayout.visibility = View.VISIBLE
                    binding.avatarThreeLayout.visibility = View.VISIBLE
                    loadImage(binding.multipleUser1ImageView, memberImages.getOrNull(0)?.memberImage)
                    loadImage(binding.multipleUser2ImageView, memberImages.getOrNull(1)?.memberImage)
                    binding.countTextView.text = "+${(currentMemberCnt - 2).coerceAtLeast(0)}"
                }
            }
        }

        private fun loadImage(imageView: ImageView, url: String?) {
            if (!url.isNullOrEmpty()) {
                Glide.with(imageView.context)
                    .load(url)
                    .circleCrop()
                    .into(imageView)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemEndedChallengesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }

        class DiffCallback : DiffUtil.ItemCallback<EndedChallengeContent>() {
            override fun areItemsTheSame(
                oldItem: EndedChallengeContent,
                newItem: EndedChallengeContent
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: EndedChallengeContent,
                newItem: EndedChallengeContent
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}