package com.example.alloon_aos.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignUpBinding
import com.example.alloon_aos.view.activity.HomeActivity
import com.example.alloon_aos.viewmodel.MainViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding : FragmentSignUpBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.fragment = this

        val mActivity = activity as HomeActivity
        mActivity.setAppBar("회원가입")

        return binding.root
    }

}