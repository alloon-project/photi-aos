package com.photi.aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.photi.aos.R
import com.photi.aos.databinding.FragmentIntroduceBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.FeedViewModel


class IntroduceFragment : Fragment() {
    private lateinit var binding : FragmentIntroduceBinding
    private lateinit var mContext: Context
     private val feedViewModel by activityViewModels<FeedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_introduce, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setObserve()


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setObserve() {
        feedViewModel.code.observe(viewLifecycleOwner) { code ->
            handleApiError(code)
        }

        feedViewModel.challengeInfo.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                val rules = data.rules.map { rule -> rule.rule }

                val ruleTextViews = listOf(
                    binding.rule1TextView,
                    binding.rule2TextView,
                    binding.rule3TextView,
                    binding.rule4TextView,
                    binding.rule5TextView
                )

                ruleTextViews.forEachIndexed { index, textView ->
                    if (index < rules.size) {
                        textView.text = rules[index]
                        textView.visibility = View.VISIBLE
                    } else {
                        textView.visibility = View.GONE
                    }
                }

                if(data.goal.isNotEmpty()){
                    binding.goalTextView.text = data.goal
                    binding.goalTextView.setTextColor(mContext.getColor(R.color.gray600))
                }
                binding.proveTimeTextView.text = data.proveTime.replace(":", " : ")
                binding.dateTextView.text = data.startDate.replace("-",". ") + " ~ " + data.endDate.replace("-",". ")
            }

        }
    }
    private fun handleApiError(code: String) {
        val errorMessages = mapOf(
            "CHALLENGE_NOT_FOUND" to "존재하지 않는 챌린지입니다.",
            "UNKNOWN_ERROR" to "알 수 없는 오류가 발생했습니다.",
            "TOKEN_UNAUTHENTICATED" to "승인되지 않은 요청입니다. 다시 로그인 해주세요.",
            "TOKEN_UNAUTHORIZED" to "권한이 없는 요청입니다. 로그인 후 다시 시도해주세요.",
        )

        if (code == "200 OK")   return

        if (code == "IO_Exception") {
            CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
        } else {
            val message = errorMessages[code] ?: "예기치 않은 오류가 발생했습니다. ($code)"
            Log.e("IntroduceFragment", "Error: $message")
        }
    }

}