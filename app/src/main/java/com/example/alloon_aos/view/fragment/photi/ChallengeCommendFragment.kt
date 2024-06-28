package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeCommendBinding
import com.example.alloon_aos.view.activity.PhotiActivity

class ChallengeCommendFragment : Fragment() {
    private lateinit var binding : FragmentChallengeCommendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_commend, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        return binding.root
    }
}