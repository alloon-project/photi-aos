package com.example.alloon_aos.view.fragment.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentProfileModifyBinding
import com.example.alloon_aos.databinding.FragmentUnSubscribeBinding

class ProfileModify : Fragment() {
    private lateinit var binding : FragmentProfileModifyBinding
    private lateinit var mContext: Context
    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_modify, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun moveFlag(i : Int){
        when(i){
            1 -> {
                view?.findNavController()?.navigate(R.id.action_profileModifyFragment_to_profilePasswordFragment)
            }
            2 -> {
                view?.findNavController()?.navigate(R.id.action_profileModifyFragment_to_unSubscribeFragment)
            }
        }
    }
}