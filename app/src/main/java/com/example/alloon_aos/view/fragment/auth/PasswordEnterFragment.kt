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
import com.example.alloon_aos.databinding.FragmentPasswordChangeBinding
import com.example.alloon_aos.databinding.FragmentPasswordEnterBinding
import com.example.alloon_aos.view.CustomDialog
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

        setListener()

        return binding.root
    }

    fun go(){
        view?.findNavController()?.navigate(R.id.action_passwordEnterFragment_to_passwordChangeFragment)
    }
    fun setListener(){
//        binding.newPassword1EditText.setOnFocusChangeListener { v, hasFocus ->
//            if(hasFocus) binding.newPasswordEditText.background = resources.getDrawable(R.drawable.input_line_focus)
//            else    binding.newPasswordEditText.background = resources.getDrawable(R.drawable.input_line_default)
//        }
//
//        binding.newPasswordEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                //checkEmailValidation()
//                //binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
//            }
//        })

    }

}