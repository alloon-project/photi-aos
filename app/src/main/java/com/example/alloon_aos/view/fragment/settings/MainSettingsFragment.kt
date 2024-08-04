package com.example.alloon_aos.view.fragment.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentMyInfoBinding

class MainSettingsFragment : Fragment() {
    private lateinit var binding : FragmentMyInfoBinding
    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_info, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        view?.findNavController()?.navigate(R.id.action_unSubscribeFragment_to_mainSettingsFragment)
        return binding.root
    }

    fun moveFrag(i : Int){
        if(i == 1){
            view?.findNavController()?.navigate(R.id.action_mainSettingsFragment_to_inquireFragment)
        }
        else if(i == 2){
            view?.findNavController()?.navigate(R.id.action_mainSettings_to_unSubscribeFragment)
        }
    }

}