package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.databinding.DialogEndedChallengesBinding
import com.example.alloon_aos.databinding.ItemEndedChallengesBinding

class EndedChallengesDialog(val count : Int, val challenge_id : Int): DialogFragment() {
    private var _binding: DialogEndedChallengesBinding? = null
    private val binding get() = _binding!!
    data class EndedChallenge(
        val title: String,
        val url : String,
        val date: String, // 날짜를 문자열로 저장 (예: "2024.04.01")
        val memberCount: Int // 파티원 수
    )
    val list = listOf(
        EndedChallenge("산책챌린지","https://ifh.cc/g/6HRkxa.jpg", "2024.06.30", 1),
        EndedChallenge("독서챌린지", "https://ifh.cc/g/AA0NMd.jpg","2024.07.15", 3),
        EndedChallenge("요리챌린지", "https://ifh.cc/g/09y6Mo.jpg","2024.08.01", 12),
        EndedChallenge("러닝챌린지", "https://ifh.cc/g/KB2Vh1.jpg","2024.09.10", 2),
        EndedChallenge("명상챌린지", "https://ifh.cc/g/yxgmBH.webp","2024.10.05", 20)
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogEndedChallengesBinding.inflate(inflater, container, false)
        val view = binding.root

        with(binding){
            countTextView.text = count.toString()
            challengeRecyclerview.adapter = EndedChallengesAdpater(list)
            challengeRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            challengeRecyclerview.setHasFixedSize(true)
            backImgBtn.setOnClickListener {
                dismiss()
            }
        }

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
        _binding = null
    }

    class EndedChallengesAdpater(val list: List<EndedChallenge>): RecyclerView.Adapter<EndedChallengesAdpater.ViewHolder>() {
        inner class ViewHolder(var binding: ItemEndedChallengesBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setContents(holder: ViewHolder, pos: Int) {
                with(list[pos]) {
                    binding.titleTextView.text = title
                    binding.dateTextView.text = date
                    Glide
                        .with(holder.itemView.context)
                        .load(url)
                        .into(binding.imgView)

                    when (memberCount) {
                        1 -> {
                            binding.avatarOneLayout.visibility = View.VISIBLE
                        }
                        2-> {
                            binding.avatarTwoLayout.visibility = View.VISIBLE
                        }
                        3 -> {
                            binding.avatarThreeLayout.visibility = View.VISIBLE
                        }
                        else -> {
                            binding.avatarMultipleLayout.visibility = View.VISIBLE
                            binding.countTextView.text = "+"+(memberCount - 2).toString()
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemEndedChallengesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder, position)
        }
        override fun getItemCount() = list.size
    }
}