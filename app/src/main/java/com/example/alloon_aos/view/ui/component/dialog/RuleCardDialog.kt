package com.example.alloon_aos.view.ui.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.databinding.DialogCardRuleBinding
import com.example.alloon_aos.databinding.ItemInquiryRuleRecyclerviewBinding
import com.example.alloon_aos.viewmodel.ChallengeViewModel


class RuleCardDialog(val challengeViewModel: ChallengeViewModel) : DialogFragment() {
    private var _binding: DialogCardRuleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogCardRuleBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        binding.ruleRecyclerview.adapter = RuleCardAdapter()
        binding.ruleRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.ruleRecyclerview.setHasFixedSize(true)

        binding.exitButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            // 전체 화면의 너비
            val fullWidth = displayMetrics.widthPixels

            // 양옆 32dp를 제외한 너비
            val marginHorizontal = (24 * density).toInt()
            val width = fullWidth - 2 * marginHorizontal

            // 다이얼로그의 레이아웃 속성 설정
            val params = window.attributes
            params.dimAmount = 0.4f // 배경의 투명도 설정 (0.0f부터 1.0f까지)
            window.attributes = params

            // 다이얼로그의 크기 설정
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ViewHolder(private val binding: ItemInquiryRuleRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (challengeViewModel.rules[pos]) {
                binding.ruleTextview.text = rule
            }
        }
    }

    inner class RuleCardAdapter() : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemInquiryRuleRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(position)
        }

        override fun getItemCount() = challengeViewModel.rules.size
    }
}