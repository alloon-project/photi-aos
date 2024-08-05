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

        authViewModel.resetCodeValue()
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
                    binding.idErrorTextview.isVisible = false
                    binding.checkBtn.isEnabled = true
                }
                else    binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
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

                    "USERNAME_LENGTH_INVALID" ->{
                        binding.idErrorTextview.isVisible = true
                        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                        binding.nextBtn.isEnabled = false
                        binding.checkBtn.isEnabled = false
                        binding.idErrorTextview.setTextColor(red)
                        binding.idErrorTextview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_default, 0, 0, 0)
                        binding.idErrorTextview.text = "아이디는 5~20자만 가능합니다"
                    }

                    "USERNAME_FORMAT_INVALID"->{
                        binding.idErrorTextview.isVisible = true
                        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                        binding.nextBtn.isEnabled = false
                        binding.checkBtn.isEnabled = false
                        binding.idErrorTextview.setTextColor(red)
                        binding.idErrorTextview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_default, 0, 0, 0)
                        binding.idErrorTextview.text = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요"
                    }

                    //이미 사용중인 아이디 코드 확정 아님!!!!
                    "UNAVAILABLE_USERNAME" -> {
                        binding.idErrorTextview.isVisible = true
                        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
                        binding.nextBtn.isEnabled = false
                        binding.checkBtn.isEnabled = false
                        binding.idErrorTextview.setTextColor(red)
                        binding.idErrorTextview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_default, 0, 0, 0)
                        binding.idErrorTextview.text = "이미 사용중인 아이디예요"
                    }

                    "USERNAME_AVAILABLE" -> {
                        binding.idErrorTextview.isVisible = true
                        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                        binding.idErrorTextview.text = "사용할 수 있는 아이디예요"
                        binding.idErrorTextview.setTextColor(blue)
                        binding.idErrorTextview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue, 0, 0, 0)
                        binding.nextBtn.isEnabled = true
                    }
                }
            }
        }
    }
}