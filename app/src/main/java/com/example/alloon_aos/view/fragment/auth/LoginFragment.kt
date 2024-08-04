package com.example.alloon_aos.view.fragment.auth

import android.content.Context
import android.content.Intent
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
import androidx.navigation.findNavController
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentLoginBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.PhotiActivity
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
//        MyApplication.mySharedPreferences.remove("access_token")
//        MyApplication.mySharedPreferences.remove("refresh_token")
        
        authViewModel.resetAllValue()
        setObserve()
        setListener()

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
                authViewModel.resetAllValue()
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

    fun setListener(){
        binding.idEdittext.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.idEdittext.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.idEdittext.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.idErrorTextview.isVisible = false
            }

        binding.pwEdittext.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.pwEdittext.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.pwEdittext.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.pwErrorTextview.isVisible = false
            }

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

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }


    fun setObserve(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "USERNAME_FIELD_REQUIRED", "PASSWORD_FIELD_REQUIRED" -> {
                        CustomToast.createToast(activity, "아이디와 비밀번호 모두 입력해주세요")?.show()
                    }
                    "USER_LOGIN" -> {
                        val token = MyApplication.mySharedPreferences.getString("Authorization","no")
                        Log.d("TAG","token : $token")
                        val intent = Intent(requireContext(), PhotiActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    "LOGIN_UNAUTHENTICATED" -> {
                        binding.idEdittext.setBackgroundResource(R.drawable.input_line_error)
                        binding.pwEdittext.setBackgroundResource(R.drawable.input_line_error)
                        binding.idErrorTextview.isVisible = true
                        binding.pwErrorTextview.isVisible = true
                    }
                    "IO_Exception" ->{
                        CustomToast.createToast(activity,"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }
}