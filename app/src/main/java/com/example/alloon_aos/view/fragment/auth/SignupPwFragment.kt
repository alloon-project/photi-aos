package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupPwBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupPwFragment : Fragment() {
    private lateinit var binding : FragmentSignupPwBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_pw, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        authViewModel.resetCodeValue()
        setObserve()

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signupPwFragment_to_loginFragment)
        }

        ObjectAnimator.ofInt(binding.pwProgress, "progress", 60,80)
            .setDuration(500)
            .start()

        binding.hideBtn.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn.setTag("1")
                    binding.pwEdittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_on)
                }
                "1" -> {
                    binding.hideBtn.setTag("0")
                    binding.pwEdittext.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_off)
                }
            }
            binding.pwEdittext.setSelection(binding.pwEdittext.text!!.length)
        }

        binding.hideBtn2.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn.setTag("1")
                    binding.checkPwEdittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_on)
                }
                "1" -> {
                    binding.hideBtn.setTag("0")
                    binding.checkPwEdittext.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_off)
                }
            }
            binding.checkPwEdittext.setSelection(binding.checkPwEdittext.text!!.length)
        }

        return binding.root
    }

    fun setObserve(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }
}