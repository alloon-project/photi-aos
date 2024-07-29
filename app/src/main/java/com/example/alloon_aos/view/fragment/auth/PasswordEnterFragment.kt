package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentPasswordEnterBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel


class PasswordEnterFragment : Fragment() {
    private lateinit var binding : FragmentPasswordEnterBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_enter, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        authViewModel.resetCodeValue()
        authViewModel.password = ""
        setListener()
        setObserver()
        return binding.root
    }

    fun setListener(){
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                binding.errorTextView.visibility = View.INVISIBLE
                if (visible) {
                    binding.newPasswordEditText.background =
                        resources.getDrawable(R.drawable.input_line_focus)
                } else {
                    binding.newPasswordEditText.background = resources.getDrawable(R.drawable.input_line_default)
                    if(binding.newPasswordEditText.text.isNotEmpty()) binding.nextButton.isEnabled = true
                    else binding.nextButton.isEnabled = false
                }
            }
        })
    }

    fun setObserver(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it){
                    "PASSWORD_SENT" -> {
                        CustomToast.createToast(getActivity(),"인증메일이 재전송되었어요")?.show()
                    }
                    "EMAIL_FIELD_REQUIRED" -> {
                        CustomToast.createToast(getActivity(),"이메일은 필수 입력입니다")?.show()
                    }
                    "USERNAME_FIELD_REQUIRED" ->{
                        CustomToast.createToast(getActivity(),"아이디는 필수 입력입니다")?.show()

                    }
                    "USER_NOT_FOUND" -> {
                        CustomToast.createToast(getActivity(),"아이디 혹은 이메일이 일치하지 않아요")?.show()
                    }
                    "EMAIL_SEND_ERROR" ->{
                        CustomToast.createToast(getActivity(),"이메일 전송 중 서버 에러가 발생했습니다")?.show()
                    }
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                    "USER_LOGIN" -> {
                        view?.findNavController()?.navigate(R.id.action_passwordEnterFragment_to_passwordChangeFragment)
                    }
                    "LOGIN_UNAUTHENTICATED","PASSWORD_FIELD_REQUIRED" -> {
                        //비밀번호 불일치
                        binding.newPasswordEditText.setBackgroundResource(R.drawable.input_line_error)
                        binding.errorTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}