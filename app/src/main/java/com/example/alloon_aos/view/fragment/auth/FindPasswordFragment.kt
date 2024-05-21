package com.example.alloon_aos.view.fragment.auth

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindPasswordBinding
import com.example.alloon_aos.view.CustomDialog
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel


class FindPasswordFragment : Fragment() {
    private lateinit var binding : FragmentFindPasswordBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 찾기")

        setListener()
        setObserver()

        return binding.root
    }

    fun setListener(){
        binding.idEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.idEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.idEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.newPasswordEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        setKeyboardVisibilityListener(object : OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if(!visible)
                    checkEmailValidation()
            }
        })
    }

    fun setObserver(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it){
                    "PASSWORD_SENT" -> {
                        CustomToast.createToast(getActivity(),"인증 메일이 재전송되었어요.")?.show()
                        binding.userEmailTextView.setText(authViewModel.email)
                        binding.sendCodeLayout.visibility = View.GONE
                        binding.enterCodeLayout.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE
                        binding.movefragmentButton.visibility = View.VISIBLE
                    }
                    "EMAIL_FIELD_REQUIRED" -> {
                        CustomToast.createToast(getActivity(),"이메일은 필수 입력입니다.")?.show()
                    }
                    "USERNAME_FIELD_REQUIRED" ->{
                        CustomToast.createToast(getActivity(),"아이디는 필수 입력입니다.")?.show()

                    }
                    "USER_NOT_FOUND" -> {
                        CustomToast.createToast(getActivity(),"아이디 혹은 이메일이 일치하지 않아요.")?.show()
                    }
                    "EMAIL_SEND_ERROR" ->{
                        CustomToast.createToast(getActivity(),"이메일 전송 중 서버 에러가 발생했습니다.")?.show()
                    }
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }

    fun moveToChangePassWordFragment(){
        view?.findNavController()?.navigate(R.id.action_findPasswordFragment_to_changePasswordFragment)
    }

    fun checkEmailValidation(){
        var email =binding.emailEditText.text.toString().trim()
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.errorTextView.visibility = View.GONE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true

        } else {
            binding.errorTextView.text = resources.getString(R.string.emailerror1)
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false

        }
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