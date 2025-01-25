package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentIntroduceBinding
import com.example.alloon_aos.viewmodel.FeedViewModel


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

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setObserve() {
        feedViewModel.code.observe(this) { code ->
            when (code) {
                "200 OK" -> {

                }

                "CHALLENGE_NOT_FOUND" -> {
                    Log.e("IntroduceFragment", "Error: CHALLENGE_NOT_FOUND - 존재하지 않는 챌린지입니다.")
                }

                else -> {
                    Log.e("IntroduceFragment", "Error: $code - 예기치 않은 오류가 발생했습니다.")
                }
            }
        }

        feedViewModel.challengeInfo.observe(this) { data ->
            if (data != null) {
                val rules = data.rules.map { rule -> rule.rule }

                val ruleTextViews = listOf(
                    binding.rule1TextView,
                    binding.rule2TextView,
                    binding.rule3TextView
                )

                ruleTextViews.forEachIndexed { index, textView ->
                    if (index < rules.size) {
                        textView.text = rules[index]
                        textView.visibility = View.VISIBLE
                    } else {
                        textView.visibility = View.GONE
                    }
                }

                binding.goalTextView.text = data.goal
                binding.proveTimeTextView.text = data.proveTime //사이에 공백 추가해야함

            }

        }
    }

}