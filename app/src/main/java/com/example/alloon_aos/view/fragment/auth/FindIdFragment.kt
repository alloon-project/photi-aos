package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindIdBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.CustomDialog
import com.example.alloon_aos.view.CustomOneButtonDialogInterface
import com.example.alloon_aos.view.KeyboardListener
import com.example.alloon_aos.view.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.AuthViewModel

class FindIdFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentFindIdBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

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

        setListener()
        setObserve()

        return binding.root
    }



    fun setListener() {
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.errorTextView.visibility = View.INVISIBLE
                    binding.emailEditText.background =
                        resources.getDrawable(R.drawable.input_line_focus)
                } else if(binding.emailEditText.text.isNotEmpty()){
                    checkEmailValidation()
                }
            }
        })
    }

    fun setObserve(){
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
            binding.errorTextView.visibility = View.INVISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true

        } else {
            binding.errorTextView.text = resources.getString(R.string.emailerror1)
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false

        }
    }

    override fun onClickYesButton() {
        view?.findNavController()?.navigate(R.id.action_findIdFragment_to_loginFragment)
    }



}