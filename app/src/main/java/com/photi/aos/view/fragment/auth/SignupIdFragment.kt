package com.photi.aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.photi.aos.R
import com.photi.aos.databinding.FragmentSignupIdBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.viewmodel.AuthViewModel

class SignupIdFragment : Fragment() {
    private lateinit var binding : FragmentSignupIdBinding
    private lateinit var mContext : Context
    private val authViewModel by activityViewModels<AuthViewModel>()

    private var blue  = 0
    private var red = 0

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

        blue  = mContext.getColor(R.color.blue400)
        red = mContext.getColor(R.color.red400)

        authViewModel.resetApiResponseValue()
        setObserve()
        setListener()

        ObjectAnimator.ofInt(binding.idProgress, "progress", 40,60)
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

        binding.nextBtn.setOnClickListener {
            authViewModel.resetPwValue()
            view?.findNavController()?.navigate(R.id.action_signupIdFragment_to_signupPwFragment)
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                    binding.idLinearlayout.isVisible = false
                } else {
                    binding.checkBtn.isEnabled = true
                    binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_default)

                    val length = binding.idEdittext.text.length
                    if (length < 5 || length > 20) {
                        setErrorMsg()
                        binding.idErrorTextview.text = "아이디는 5~20자만 가능해요"
                    }

                    val regex = "^[a-z0-9_]+$".toRegex()
                    if (!binding.idEdittext.text.matches(regex)) {
                        setErrorMsg()
                        binding.idErrorTextview.text = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요"
                    }
                }
            }
        })

        binding.idEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nextBtn.isEnabled = false
                binding.checkBtn.isEnabled = false
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    binding.idLinearlayout.isVisible = true
                    binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                    binding.idErrorTextview.text = "사용할 수 있는 아이디예요"
                    binding.idErrorTextview.setTextColor(blue)
                    binding.idIconView.setImageResource(R.drawable.ic_check_blue)
                    binding.nextBtn.isEnabled = true
                }

                "EXISTING_USERNAME", "UNAVAILABLE_USERNAME" -> {
                    setErrorMsg()
                    binding.idErrorTextview.text = "이미 가입된 아이디예요"
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

    private fun setErrorMsg() {
        binding.idLinearlayout.isVisible = true
        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
        binding.nextBtn.isEnabled = false
        binding.checkBtn.isEnabled = false
        binding.idErrorTextview.setTextColor(red)
        binding.idIconView.setImageResource(R.drawable.ic_close_default)
    }
}