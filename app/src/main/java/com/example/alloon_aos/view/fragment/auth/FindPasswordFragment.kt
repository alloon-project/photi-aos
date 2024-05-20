package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindPasswordBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel


class FindPasswordFragment : Fragment() {
    private lateinit var binding : FragmentFindPasswordBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.fragment = this

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        CustomToast.createToast(getActivity(),"뿅")?.show()
        return binding.root
    }
}