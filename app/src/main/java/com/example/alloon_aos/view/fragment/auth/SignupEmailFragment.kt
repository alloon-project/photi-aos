package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupEmailBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.ui.component.bottomsheet.ListBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.ListBottomSheetInterface
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupEmailFragment : Fragment() {
    private lateinit var binding : FragmentSignupEmailBinding
    private lateinit var mContext: Context
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_email, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        authViewModel.resetApiResponseValue()
        setObserve()
        setListener()

        ObjectAnimator.ofInt(binding.emailProgress, "progress", 0,20)
            .setDuration(500)
            .start()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener() {
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.emailEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                    binding.emailLinearlayout.isVisible = false
                }
                else {
                    binding.emailEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
                    checkEmailValidation()
                }
            }
        })

        binding.emailEdittext.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.emailEdittext.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.emailEdittext.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.emailLinearlayout.isVisible = false
            }

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    fun setObserve() {
        authViewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "201 CREATED" -> {
                    view?.findNavController()?.navigate(R.id.action_signupEmailFragment_to_signupAuthFragment)
                }
                "EXISTING_EMAIL" -> {
                    binding.emailLinearlayout.isVisible = true
                    binding.emailEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                    binding.nextBtn.isEnabled = false
                    binding.emailErrorTextview.text = "이미 가입된 이메일이에요"
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
        var email =binding.emailEdittext.text.toString().trim()
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.emailLinearlayout.isVisible = false
            binding.emailEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            binding.nextBtn.isEnabled = true
        } else {
            binding.emailLinearlayout.isVisible = true
            binding.emailEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
            binding.nextBtn.isEnabled = false
            if (email.length > 100)
                binding.emailErrorTextview.text = "100자 이하의 이메일을 사용해주세요"
            else
                binding.emailErrorTextview.text = "이메일 형태가 올바르지 않아요"

        }
    }
    
}