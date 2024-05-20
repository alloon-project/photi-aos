package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupEmailBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupEmailFragment : Fragment() {
    private lateinit var binding : FragmentSignupEmailBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_email, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signupEmailFragment_to_signupAuthFragment)
        }

        ObjectAnimator.ofInt(binding.emailProgress, "progress", 0,20)
            .setDuration(500)
            .start()

        return binding.root
    }
}