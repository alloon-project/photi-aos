package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupIdBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupIdFragment : Fragment() {
    private lateinit var binding : FragmentSignupIdBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_id, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        authViewModel.resetCodeValue()
        setObserve()
        setListener()

        ObjectAnimator.ofInt(binding.idProgress, "progress", 40,60)
            .setDuration(500)
            .start()

        return binding.root
    }

    fun setListener() {
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signupIdFragment_to_signupPwFragment)
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.idEdittext.background = resources.getDrawable(R.drawable.input_line_focus)
                    binding.idErrorTextview.isVisible = false
                    binding.checkBtn.isEnabled = true
                }
                else    binding.idEdittext.background = resources.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.checkBtn.setOnClickListener {
            it.isEnabled = false
        }
    }

    fun setObserve() {
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                    //아래 두개 해주세요
                    "USERNAME_LENGTH_INVALID" ->{
                        //"아이디는 5~20자만 가능합니다."
                    }
                    "USERNAME_FORMAT_INVALID"->{
                        //"아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요."
                    }
                    "USERNAME_AVAILABLE" -> {
                        //사용할 수 있는 아이디입니다메시지띄우는부분
                        binding.nextBtn.isEnabled = true
                    }
                }
            }
        }
    }
}