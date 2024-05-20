package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindIdBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.fragment.CustomDialog
import com.example.alloon_aos.view.fragment.CustomDialogInterface
import com.example.alloon_aos.viewmodel.AuthViewModel

class FindIdFragment : Fragment(), CustomDialogInterface {
    private lateinit var binding : FragmentFindIdBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("아이디 찾기")

        setObserve()

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailValidation()
            }
        })
        

        return binding.root
    }


    fun setObserve(){
        authViewModel.toast_message.observe(viewLifecycleOwner){
            if(it.isNotEmpty())
                CustomToast.createToast(getActivity(),it)?.show()
        }
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it){
                    "USERNAME_SENT" -> {
                        CustomDialog(this,"이메일로 회원정보를 보내드렸어요","다시 로그인해주세요","확인")
                            .show(activity?.supportFragmentManager!!, "CustomDialog")
                    }
                    "USER_NOT_FOUND" ->{
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
                        binding.errorTextView.text = resources.getString(R.string.emailerror2)
                    }
                    "EMAIL_SEND_ERROR" -> {
                        CustomToast.createToast(getActivity(),"이메일 전송 중 서버 에러가 발생했습니다.")?.show()
                    }
                }
            }
        }
    }

    fun checkEmailValidation():Boolean{
        var email =binding.emailEditText.text.toString().trim() //공백제거
        val pattern = Patterns.EMAIL_ADDRESS
        //binding.emailEditText.setTextColor(resources.getColor(R.color.black))

        if (pattern.matcher(email).matches()) {
            binding.errorTextView.visibility = View.INVISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true

            return true
        } else {
            binding.errorTextView.text = resources.getString(R.string.emailerror1)
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false

            return false
        }
    }

    override fun onClickYesButton() {
        //확인해야함
        view?.findNavController()?.navigate(R.id.action_findIdFragment_to_loginFragment) //스택o
    }

}