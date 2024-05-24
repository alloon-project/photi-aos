package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentPasswordSendBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.KeyboardListener
import com.example.alloon_aos.view.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel


class PasswordSendFragment : Fragment() {
    private lateinit var binding : FragmentPasswordSendBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    private var email_flag : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_send, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        authViewModel.resetAllValue()
        setListener()
        setObserver()

        return binding.root
    }

    fun setListener(){
        binding.idEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.idEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.idEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
                email_flag = true
            }
            else    {
                binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
                email_flag = false
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if(visible){
                    binding.errorTextView.visibility = View.INVISIBLE
                    binding.emailEditText.background
                    if(email_flag)  binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
                }
                else if(!visible && binding.emailEditText.text.isNotEmpty())
                    checkEmailValidation()
            }
        })
    }

    fun setObserver(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it){
                    "PASSWORD_SENT" -> {
                        view?.findNavController()?.navigate(R.id.action_passwordSendFragment_to_passwordEnterFragment)
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
                }
            }
        }
    }

    fun checkEmailValidation(){
        var email =binding.emailEditText.text.toString().trim()
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.errorTextView.visibility = View.GONE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true

        } else {
            binding.errorTextView.text = resources.getString(R.string.emailerror1)
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false
        }
    }

}