package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentIntroduceBinding
import com.example.alloon_aos.databinding.FragmentViewFeedBinding


class IntroduceFragment : Fragment() {
    private lateinit var binding : FragmentIntroduceBinding
    private lateinit var mContext: Context
    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_introduce, container, false)
        binding.fragment = this
        // binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}