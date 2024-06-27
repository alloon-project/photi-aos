package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeBinding
import com.example.alloon_aos.view.activity.PhotiActivity

class ChallengeFragment : Fragment() {
    private lateinit var binding : FragmentChallengeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        return binding.root
    }
}