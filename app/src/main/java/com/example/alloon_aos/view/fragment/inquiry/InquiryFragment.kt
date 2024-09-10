package com.example.alloon_aos.view.fragment.inquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentInquiryBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.InquiryActivity
import com.example.alloon_aos.viewmodel.InquiryViewModel

class InquiryFragment : Fragment() {
    private lateinit var binding : FragmentInquiryBinding
    private val inquiryViewModel by activityViewModels<InquiryViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inquiry, container, false)
        binding.fragment = this
        binding.viewModel = inquiryViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as InquiryActivity
        mActivity.setAppBar(" ")

        setObserver()

        return binding.root
    }

    private fun setObserver() {

    }

}