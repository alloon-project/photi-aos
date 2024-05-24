package com.example.alloon_aos.view.fragment.auth

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentLoginBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as AuthActivity
        mActivity.setAppBar("로그인")

        //token 초기화
        MyApplication.mySharedPreferences.remove("access_token")
        MyApplication.mySharedPreferences.remove("refresh_token")
        
        authViewModel.resetAllValue()
        setObserve()

        binding.loginId.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.loginId.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.errorId.isVisible = false
            }

        binding.loginPw.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.loginPw.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.errorPw.isVisible = false
            }

        binding.hideBtn.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn.setTag("1")
                    binding.loginPw.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_on)
                }
                "1" -> {
                    binding.hideBtn.setTag("0")
                    binding.loginPw.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_off)
                }
            }

            binding.loginPw.setSelection(binding.loginPw.text!!.length)
        }

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
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
                ft?.navigate(R.id.action_loginFragment_to_signupEmailFragment)
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
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "USERNAME_FIELD_REQUIRED", "PASSWORD_FIELD_REQUIRED" -> {
                        CustomToast.createToast(getActivity(), "아이디와 비밀번호 모두 입력해주세요")?.show()
                    }
                    "USER_LOGIN" -> {
                        Log.d("TAG","홈 화면으로 이동~")
                    }
                    "LOGIN_UNAUTHENTICATED" -> {
                        binding.loginId.setBackgroundResource(R.drawable.input_line_error)
                        binding.loginPw.setBackgroundResource(R.drawable.input_line_error)
                        binding.errorId.isVisible = true
                        binding.errorPw.isVisible = true
                    }
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }
}