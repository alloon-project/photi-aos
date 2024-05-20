package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChangePasswordBinding
import com.example.alloon_aos.databinding.FragmentFindPasswordBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.fragment.CustomDialog
import com.example.alloon_aos.view.fragment.CustomDialogInterface
import com.example.alloon_aos.viewmodel.AuthViewModel

class ChangePasswordFragment : Fragment(), CustomDialogInterface {
    private lateinit var binding : FragmentChangePasswordBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 재설정")

        setListener()

        binding.nextButton.isEnabled = true

        return binding.root
    }


    fun setListener(){
        binding.newPasswordEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.newPasswordEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.newPasswordEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.newPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //checkEmailValidation()
                //binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            }
        })

    }

    fun goLogin(){
        CustomDialog(this,"비밀번호가 변경되었어요","새 비밀번호로 로그인해주세요","확인")
            .show(activity?.supportFragmentManager!!, "CustomDialog")
    }

    override fun onClickYesButton() {
        view?.findNavController()?.navigate(R.id.action_changePasswordFragment_to_loginFragment)
    }
}