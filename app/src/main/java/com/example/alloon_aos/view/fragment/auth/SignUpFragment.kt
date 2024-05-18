package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignUpBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding : FragmentSignUpBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("회원가입")

        setObserver()
        binding.click.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signUpFragment_to_loginFragment) //스택o
            //view?.findNavController()?.popBackStack(R.id.loginFragment, false) //스택x
        }


        return binding.root
    }


    fun setObserver(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                authViewModel.toast_message.observe(viewLifecycleOwner){
                    if(it.isNotEmpty())
                        Toast.makeText(getActivity(), it, Toast.LENGTH_SHORT).show()
                }

                authViewModel.code.observe(viewLifecycleOwner){
                    if(it.isNotEmpty()){
                        when(it){
                            "EMAIL_VERIFICATION_CODE_SENT" -> { //이메일 인증코드를 보냈습니다.
                                Toast.makeText(getActivity(), "EMAIL_VERIFICATION_CODE_SENT", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_FIELD_REQUIRED" -> {
                                Toast.makeText(getActivity(), "EMAIL_FIELD_REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_LENGTH_INVALID" -> {
                                Toast.makeText(getActivity(), "EMAIL_LENGTH_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_FORMAT_INVALID" -> {
                                Toast.makeText(getActivity(), "EMAIL_FORMAT_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "EXISTING_EMAIL" -> {
                                Toast.makeText(getActivity(), "EXISTING_EMAIL", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_SEND_ERROR" -> {
                                Toast.makeText(getActivity(), "EMAIL_SEND_ERROR", Toast.LENGTH_SHORT).show()
                            }



                            "EMAIL_VERIFICATION_CODE_VERIFIED" -> {//이메일 인증코드 검증
                                Toast.makeText(getActivity(), "EMAIL_VERIFICATION_CODE_VERIFIED", Toast.LENGTH_SHORT).show()
                            }

                            "VERIFICATION_CODE_FIELD_REQUIRED" -> {
                                Toast.makeText(getActivity(), "VERIFICATION_CODE_FIELD_REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_VERIFICATION_CODE_INVALID" -> {
                                Toast.makeText(getActivity(), "EMAIL_VERIFICATION_CODE_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_NOT_FOUND" -> {
                                Toast.makeText(getActivity(), "EMAIL_NOT_FOUND", Toast.LENGTH_SHORT).show()
                            }


                            "USERNAME_AVAILABLE" -> {//사용 가능 아이디
                                println("11")
                                Toast.makeText(getActivity(), "USERNAME_AVAILABLE", Toast.LENGTH_SHORT).show()
                            }
                            "USERNAME_FIELD_REQUIRED" -> {
                                Toast.makeText(getActivity(), "USERNAME_FIELD_REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                            "USERNAME_LENGTH_INVALID" -> {
                                Toast.makeText(getActivity(), "USERNAME_LENGTH_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "USERNAME_FORMAT_INVALID" -> {
                                Toast.makeText(getActivity(), "USERNAME_FORMAT_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "UNAVAILABLE_USERNAME" -> {
                                Toast.makeText(getActivity(), "UNAVAILABLE_USERNAME", Toast.LENGTH_SHORT).show()
                            }
                            "EXISTING_USERNAME" -> {
                                Toast.makeText(getActivity(), "EXISTING_USERNAME", Toast.LENGTH_SHORT).show()
                            }

                            "USER_REGISTERED" -> {//회원가입 완료
                                Toast.makeText(getActivity(), "USER_REGISTERED", Toast.LENGTH_SHORT).show()
                            }
                            "PASSWORD_FIELD_REQUIRED" -> {
                                Toast.makeText(getActivity(), "PASSWORD_FIELD_REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                            "PASSWORD_FORMAT_INVALID" -> {
                                Toast.makeText(getActivity(), "PASSWORD_FORMAT_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "PASSWORD_RE_ENTER_FIELD_REQUIRED" -> {
                                Toast.makeText(getActivity(), "PASSWORD_RE_ENTER_FIELD_REQUIRED", Toast.LENGTH_SHORT).show()
                            }
                            "EMAIL_VALIDATION_INVALID" -> {
                                Toast.makeText(getActivity(), "EMAIL_VALIDATION_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "PASSWORD_MATCH_INVALID" -> {
                                Toast.makeText(getActivity(), "PASSWORD_MATCH_INVALID", Toast.LENGTH_SHORT).show()
                            }
                            "EXISTING_USER" -> {
                                Toast.makeText(getActivity(), "EXISTING_USER", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }

            }
        }
    }

}