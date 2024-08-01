package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentHomeBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity


        CustomToast.createToast(getActivity(),tokenManager.getAccessToken().toString())?.show()
        return binding.root
    }
}