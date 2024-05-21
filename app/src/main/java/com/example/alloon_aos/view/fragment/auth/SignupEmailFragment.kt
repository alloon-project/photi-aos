package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.databinding.FragmentSignupEmailBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupEmailFragment : Fragment() {
    private lateinit var binding : FragmentSignupEmailBinding
    private val authViewModel by viewModels<AuthViewModel>()

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

        setListener()
        setObserve()

        ObjectAnimator.ofInt(binding.emailProgress, "progress", 0,20)
            .setDuration(500)
            .start()

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        return binding.root
    }

    fun setObserve(){
        authViewModel.toast_message.observe(viewLifecycleOwner){
            if(it.isNotEmpty())
                CustomToast.createToast(getActivity(),it)?.show()
        }
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "EMAIL_VERIFICATION_CODE_SENT" -> {
                        view?.findNavController()?.navigate(
                            SignupEmailFragmentDirections.actionSignupEmailFragmentToSignupAuthFragment(
                                UserData(authViewModel.email)))
                    }
                    "EXISTING_EMAIL" -> {
                        //이미 사용중인 이멜
                    }
                }
            }
        }
    }

    fun checkEmailValidation(){
        var email =binding.signupEmail.text.toString().trim()
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.errorEmail.isVisible = false
            binding.signupEmail.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextBtn.isEnabled = true
        } else {
            binding.errorEmail.isVisible = true
            binding.signupEmail.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextBtn.isEnabled = false
            if (email.length > 100)
                binding.errorEmail.text = "100자 이하의 이메일을 사용해주세요"
            else
                binding.errorEmail.text = "이메일 형태가 올바르지 않아요"

        }
    }

    fun setListener() {
        setKeyboardVisibilityListener(object : OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.errorEmail.isVisible = false
                    binding.signupEmail.background =
                        resources.getDrawable(R.drawable.input_line_focus)
                } else {
                    checkEmailValidation()
                }
            }
        })
    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (binding.root as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + 48
            private val rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }
}