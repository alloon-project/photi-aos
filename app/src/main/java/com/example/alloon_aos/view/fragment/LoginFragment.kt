package com.example.alloon_aos.view.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.core.view.isVisible
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
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as HomeActivity
        mActivity.setAppBar("로그인")

        setObserve()

        binding.loginId.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_focus)
                    binding.noId.isVisible = false
                } else {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_default)
                    binding.noId.isVisible = false
                }
            }

        binding.loginPw.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_focus)
                    binding.noPw.isVisible = false
                } else {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_default)
                    binding.noPw.isVisible = false
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

        return binding.root
    }

    fun moveFrag(fragNum : Int){
        /*
        * 1 : login to signUp
        * 2 : login to findId
        * 3 : login to findPassword
        * */

        val ft = view?.findNavController()
        when(fragNum){
            1 -> {
                ft?.navigate(R.id.action_loginFragment_to_signUpFragment)
            }
            2 -> {
                ft?.navigate(R.id.action_loginFragment_to_findIdFragment)
            }
            3 ->{
                ft?.navigate(R.id.action_loginFragment_to_findPasswordFragment)
            }
        }
    }

    fun setObserve(){
        mainViewModel.code.observe(viewLifecycleOwner){
            if(it != null) {
                when(it) {
                    "USER_LOGIN" -> {
                        Log.d("TAG","홈 화면으로 이동~")
                    }
                    "USERNAME_FIELD_REQUIRED" -> {
                        Log.d("TAG","아이디 필수 입력")
//                        binding.loginId.setBackgroundResource(R.drawable.input_line_error)
//                        binding.loginPw.setBackgroundResource(R.drawable.input_line_error)
//                        binding.noId.isVisible = true
//                        binding.noId.setText("아이디는 필수 입력입니다.")
//                        binding.noPw.isVisible = true
//                        binding.noPw.setText("아이디는 필수 입력입니다.")
                    }
                    "PASSWORD_FIELD_REQUIRED" -> {
                        Log.d("TAG","비밀번호 필수 입력")
//                        binding.loginId.setBackgroundResource(R.drawable.input_line_error)
//                        binding.loginPw.setBackgroundResource(R.drawable.input_line_error)
//                        binding.noId.isVisible = true
//                        binding.noId.setText("비밀번호는 필수 입력입니다.")
//                        binding.noPw.isVisible = true
//                        binding.noPw.setText("비밀번호는 필수 입력입니다.")
                    }
                    "LOGIN_UNAUTHENTICATED" -> {
                        Log.d("TAG","아이디 또는 비번틀림ㅅㄱ")
//                        binding.loginId.setBackgroundResource(R.drawable.input_line_error)
//                        binding.loginPw.setBackgroundResource(R.drawable.input_line_error)
//                        binding.noId.isVisible = true
//                        binding.noId.setText("아이디 또는 비밀번호가 틀렸습니다.")
//                        binding.noPw.isVisible = true
//                        binding.noPw.setText("아이디 또는 비밀번호가 틀렸습니다.")
                    }
                }
            }
        }
    }
}