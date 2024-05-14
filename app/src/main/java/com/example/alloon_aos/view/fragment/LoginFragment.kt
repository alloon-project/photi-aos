package com.example.alloon_aos.view.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentLoginBinding
import com.example.alloon_aos.view.activity.HomeActivity
import com.example.alloon_aos.viewmodel.MainViewModel

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.fragment = this

        val mActivity = activity as HomeActivity
        mActivity.setAppBar("로그인")


        binding.loginBtn.setOnClickListener { view: View ->
            println("홈 화면으로 이동")
        }

        binding.findId.setOnClickListener {view: View ->
            println("아이디 찾기 프래그먼트로 이동")
        }

        binding.loginId.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_default)
                }
            }

        binding.loginPw.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_default)
                }
            }

        binding.hideBtn.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn.setTag("1")
                    binding.loginPw.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.eye_on)
                }
                "1" -> {
                    binding.hideBtn.setTag("0")
                    binding.loginPw.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.eye_off)
                }
            }
            binding.loginPw.setSelection(binding.loginPw.text!!.length)
        }

        binding.signUp.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return binding.root
    }
}