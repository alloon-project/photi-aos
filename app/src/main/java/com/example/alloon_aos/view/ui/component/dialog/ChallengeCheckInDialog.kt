package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.DialogChallengeCheckInBinding
import com.example.alloon_aos.databinding.DialogOneBtnBinding
import com.example.alloon_aos.databinding.ItemChallengeCheckInBinding
import com.example.alloon_aos.databinding.ItemFeedCommentBinding
import com.example.alloon_aos.view.ui.component.dialog.FeedDetailDialog.CommentsAdapter
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.Comment

class ChallengeCheckInDialog(val count : Int, val challenge_id : Int): DialogFragment() {
    private var _binding: DialogChallengeCheckInBinding? = null
    private val binding get() = _binding!!
    val comments = arrayListOf<Comment>(
        Comment("aaa","이 책 좋네요"),
        Comment("abc","멋져요"),
        Comment("baa","와우"),
        Comment("Seul","우왕굳 ㅋㅋ"),
        Comment("HB","짱~!"),
        Comment("aaa","엄청긴댓글입니다아홉열열하나다여"),
        Comment("aaa","이 책 좋네요"),
        Comment("abc","멋져요"),
        Comment("aaa","엄청긴댓글입니다아홉열열하나다여")
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogChallengeCheckInBinding.inflate(inflater, container, false)
        val view = binding.root

        with(binding){
            countTextView.text = count.toString()
            challengeRecyclerview.adapter = CertificationAdapter(comments)
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

    class CertificationAdapter(val comments: ArrayList<Comment>): RecyclerView.Adapter<CertificationAdapter.ViewHolder>() {
        inner class ViewHolder(var binding: ItemChallengeCheckInBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setContents(holder: ViewHolder, pos: Int) {
                with(comments[pos]) {
                    binding.dateTextView.text = id
                    binding.chipBtn.text = "크로아상 만들기"

                    binding.shortcutImgBtn.setOnClickListener{
                        //공유하기
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemChallengeCheckInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder, position)
        }
        override fun getItemCount() = comments.size
    }
}