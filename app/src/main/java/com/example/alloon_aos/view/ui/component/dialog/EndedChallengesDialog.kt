package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.data.model.response.EndedChallengeContent
import com.example.alloon_aos.databinding.DialogEndedChallengesBinding
import com.example.alloon_aos.databinding.ItemEndedChallengesBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel

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

        binding.countTextView.text = count.toString()
        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

        photiViewModel.fetchEndedChallenge()

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
        photiViewModel.resetPagingParam()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = EndedChallengesAdpater()
        binding.challengeRecyclerview.adapter = adapter
        binding.challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.challengeRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!photiViewModel.isLoading && !photiViewModel.isLastPage) {
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        photiViewModel.fetchEndedChallenge()
                    }
                }
            }
        })

    }

    private fun observeLiveData() {
        photiViewModel.endedChallenges.observe(viewLifecycleOwner) { data ->
            Log.d("observeLiveData","observeLiveData!!")
            adapter.submitList(data) // 데이터 업데이트
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
            photiViewModel.latestPage += 10
            return
        }

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("ProfileModifyFragment", "Error: $message")
        }
    }

    class EndedChallengesAdpater :
        ListAdapter<EndedChallengeContent, EndedChallengesAdpater.ViewHolder>(DiffCallback()) {

        inner class ViewHolder(private val binding: ItemEndedChallengesBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(data: EndedChallengeContent) {
                binding.titleTextView.text = data.name
                binding.dateTextView.text = data.endDate.replace("-", ".") + " 종료"
                Glide.with(binding.imgView.context)
                    .load(data.imageUrl)
                    .into(binding.imgView)

                when (data.currentMemberCnt) {
                    1 ->{
                        binding.avatarOneLayout.visibility = View.VISIBLE
                        Glide.with(binding.oneUser1ImageView.context)
                            .load(data.memberImages[0].memberImage)
                            .circleCrop()
                            .into(binding.oneUser1ImageView)
                    }
                    2 -> {
                        binding.avatarTwoLayout.visibility = View.VISIBLE
                        Glide.with(binding.twoUser1ImageView.context)
                            .load(data.memberImages[0].memberImage)
                            .circleCrop()
                            .into(binding.twoUser1ImageView)
                        Glide.with(binding.twoUser2ImageView.context)
                            .load(data.memberImages[1].memberImage)
                            .circleCrop()
                            .into(binding.twoUser2ImageView)
                    }
                    3 -> {
                        binding.avatarThreeLayout.visibility = View.VISIBLE
                        Glide.with(binding.threeUser1ImageView.context)
                            .load(data.memberImages[0].memberImage)
                            .circleCrop()
                            .into(binding.threeUser1ImageView)
                        Glide.with(binding.threeUser2ImageView.context)
                            .load(data.memberImages[1].memberImage)
                            .circleCrop()
                            .into(binding.threeUser2ImageView)
                        Glide.with(binding.threeUser3ImageView.context)
                            .load(data.memberImages[2].memberImage)
                            .circleCrop()
                            .into(binding.threeUser3ImageView)
                    }
                    else -> {
                        binding.avatarMultipleLayout.visibility = View.VISIBLE
                        binding.avatarThreeLayout.visibility = View.VISIBLE
                        Glide.with(binding.multipleUser1ImageView.context)
                            .load(data.memberImages[0].memberImage)
                            .circleCrop()
                            .into(binding.multipleUser1ImageView)
                        Glide.with(binding.multipleUser2ImageView.context)
                            .load(data.memberImages[1].memberImage)
                            .circleCrop()
                            .into(binding.multipleUser2ImageView)
                        binding.countTextView.text = "+" + (data.currentMemberCnt - 2).toString()
                    }
                }
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
            holder.bind(getItem(position))
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