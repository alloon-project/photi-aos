package com.photi.aos.view.fragment.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.photi.aos.R
import com.photi.aos.databinding.FragmentPasswordEnterBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.viewmodel.AuthViewModel


class PasswordEnterFragment : Fragment() {
    private lateinit var binding : FragmentPasswordEnterBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mContext : Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_enter, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        authViewModel.resetApiResponseValue()
        authViewModel.password = ""
        setListener()
        setObserve()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    fun setListener(){
        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                binding.emailLinearlayout.visibility = View.GONE
                if (visible) {
                    binding.newPasswordEditText.background =
                        mContext.getDrawable(R.drawable.input_line_focus)
                } else {
                    binding.newPasswordEditText.background = mContext.getDrawable(R.drawable.input_line_default)
                    if(binding.newPasswordEditText.text.isNotEmpty()) binding.nextButton.isEnabled = true
                    else binding.nextButton.isEnabled = false
                }
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    when (response.action) {
                        "login" -> {
                            view?.findNavController()?.navigate(R.id.action_passwordEnterFragment_to_passwordChangeFragment)
                        }
                        "sendEmailCode" -> {
                            CustomToast.createToast(activity, "인증메일이 재전송되었어요")?.show()
                        }
                        else -> {
                            Log.d("Observer", "Unhandled action for 200 OK: ${response.action}")
                        }
                    }
                }

                "USER_LOGIN" -> { // 미완성, 임시 비밀번호 일치
                    view?.findNavController()?.navigate(R.id.action_passwordEnterFragment_to_passwordChangeFragment)
                }

                "LOGIN_UNAUTHENTICATED", "DELETED_USER" -> {
                    binding.newPasswordEditText.setBackgroundResource(R.drawable.input_line_error)
                    binding.emailLinearlayout.visibility = View.VISIBLE
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