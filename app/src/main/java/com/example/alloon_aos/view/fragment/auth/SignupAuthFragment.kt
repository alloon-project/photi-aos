package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupAuthBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupAuthFragment : Fragment() {
    private lateinit var binding : FragmentSignupAuthBinding
    private lateinit var mContext: Context
    private val authViewModel by activityViewModels<AuthViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_auth, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        authViewModel.resetCodeValue()
        authViewModel.resetAuthCodeValue()
        setObserve()
        setListener()

        ObjectAnimator.ofInt(binding.authProgress, "progress", 20,40)
            .setDuration(500)
            .start()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener() {
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.authEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                    binding.authErrorTextview.isVisible = false
                }
                else    binding.authEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })
    }

    fun setObserve() {
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "IO_Exception" ->{
                        CustomToast.createToast(activity,"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }

                    "EMAIL_VERIFICATION_CODE_SENT" -> {
                        CustomToast.createToast(activity,"인증메일이 재전송되었어요")?.show()
                    }

                    "EMAIL_VERIFICATION_CODE_INVALID" ->{
                        binding.authErrorTextview.isVisible = true
                        binding.authEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                        binding.nextBtn.isEnabled = false
                        binding.authErrorTextview.text = "인증코드가 일치하지 않아요"
                    }

                    "EMAIL_VERIFICATION_CODE_VERIFIED" -> {
                        authViewModel.resetIdValue()
                        view?.findNavController()?.navigate(R.id.action_signupAuthFragment_to_signupIdFragment)
                    }
                }
            }
        }
    }

}