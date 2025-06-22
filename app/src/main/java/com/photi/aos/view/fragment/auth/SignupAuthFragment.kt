package com.photi.aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.photi.aos.R
import com.photi.aos.databinding.FragmentSignupAuthBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.viewmodel.AuthViewModel

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

        authViewModel.resetApiResponseValue()
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
                    binding.authLinearlayout.isVisible = false
                }
                else    binding.authEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.authEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nextBtn.isEnabled = false
                if(s!!.length == 4)
                    binding.nextBtn.isEnabled = true
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    authViewModel.resetIdValue()
                    view?.findNavController()?.navigate(R.id.action_signupAuthFragment_to_signupIdFragment)
                }

                "201 CREATED" -> {
                    CustomToast.createToast(activity, "인증메일이 재전송되었어요")?.show()
                }

                "EMAIL_VERIFICATION_CODE_INVALID" -> {
                    binding.authLinearlayout.isVisible = true
                    binding.authEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                    binding.nextBtn.isEnabled = false
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

}