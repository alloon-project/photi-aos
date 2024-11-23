package com.example.alloon_aos.view.fragment.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentPasswordSendBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.AuthViewModel


class PasswordSendFragment : Fragment() {
    private lateinit var binding : FragmentPasswordSendBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mContext : Context

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
        setObserve()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    fun setListener(){
        binding.idEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.idEditText.background = mContext.getDrawable(R.drawable.input_line_focus)
            else    binding.idEditText.background = mContext.getDrawable(R.drawable.input_line_default)
        }

        binding.emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                binding.emailEditText.background = mContext.getDrawable(R.drawable.input_line_focus)
                email_flag = true
            }
            else    {
                binding.emailEditText.background = mContext.getDrawable(R.drawable.input_line_default)
                email_flag = false
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if(visible){
                    binding.checkPwLinearlayout.visibility = View.GONE
                    binding.emailEditText.background
                    if(email_flag)  binding.emailEditText.background = mContext.getDrawable(R.drawable.input_line_focus)
                }
                else if(!visible && binding.emailEditText.text.isNotEmpty())
                    checkEmailValidation()
            }
        })
    }

    fun setObserve() {
        authViewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    view?.findNavController()?.navigate(R.id.action_passwordSendFragment_to_passwordEnterFragment)
                }

                "USER_NOT_FOUND" -> {
                    CustomToast.createToast(activity, "아이디 혹은 이메일이 일치하지 않아요")?.show()
                }

                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }

                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }


    fun checkEmailValidation(){
        var email =binding.emailEditText.text.toString().trim()
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.checkPwLinearlayout.visibility = View.GONE
            binding.emailEditText.background = mContext.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true

        } else {
            binding.checkPwLinearlayout.visibility = View.VISIBLE
            binding.emailEditText.background = mContext.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false
        }
    }

}