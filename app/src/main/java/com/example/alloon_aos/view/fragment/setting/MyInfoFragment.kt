package com.example.alloon_aos.view.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentInquireBinding
import com.example.alloon_aos.databinding.FragmentMyInfoBinding
import com.example.alloon_aos.view.activity.AuthActivity

class MyInfoFragment : Fragment() {
    private lateinit var binding : FragmentMyInfoBinding
    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_info, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun moveFrag(i : Int){
        if(i == 1){
            view?.findNavController()?.navigate(R.id.action_myInfoFragment_to_inquireFragment)
        }
        else if(i == 2){
            view?.findNavController()?.navigate(R.id.action_myInfoFragment_to_unSubscribeFragment)
        }
    }

}