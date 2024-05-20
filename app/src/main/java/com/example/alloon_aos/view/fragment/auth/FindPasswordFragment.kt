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
import com.example.alloon_aos.databinding.FragmentFindPasswordBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel


class FindPasswordFragment : Fragment() {
    private lateinit var binding : FragmentFindPasswordBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        setListener()

        return binding.root
    }

    fun setListener(){
        binding.idEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.idEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.idEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }


        binding.idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //binding.idEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //checkEmailValidation()
                //binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            }
        })
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //checkEmailValidation()
                //binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            }
        })

        binding.resendTextView.setOnClickListener {
            println("resend")
        }
    }
    fun moveToChangePassWordFragment(){
        //view?.findNavController()?.navigate(R.id.action_findPasswordFragment_to_changePasswordFragment)
        binding.userEmailTextView.setText(authViewModel.email)
        binding.sendCodeLayout.visibility = View.GONE
        binding.enterCodeLayout.visibility = View.VISIBLE
    }
}