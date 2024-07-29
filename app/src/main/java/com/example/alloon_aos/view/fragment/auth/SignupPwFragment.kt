package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupPwBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel
import java.util.regex.Pattern

class SignupPwFragment : Fragment() {
    private lateinit var binding : FragmentSignupPwBinding
    private val authViewModel by viewModels<AuthViewModel>()

    private val num_pattern = Pattern.compile("[0-9]")
    private val eng_pattern = Pattern.compile("[a-zA-Z]")
    private val spe_regex = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|@\\-\\_\\.\\;\\·ㆍᆞᆢ•‥a·﹕]*".toRegex()

    private var green  = 0
    private var gray = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_pw, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        green  = resources.getColor(R.color.green200)
        gray = resources.getColor(R.color.gray400)

        authViewModel.resetCodeValue()
        //setObserve()
        setListener()


        ObjectAnimator.ofInt(binding.pwProgress, "progress", 60,80)
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
            view?.findNavController()?.navigate(R.id.action_signupPwFragment_to_loginFragment)
        }

        binding.hideBtn.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn.setTag("1")
                    binding.pwEdittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_on)
                }
                "1" -> {
                    binding.hideBtn.setTag("0")
                    binding.pwEdittext.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn.setBackgroundResource(R.drawable.ic_eye_off)
                }
            }
            binding.pwEdittext.setSelection(binding.pwEdittext.text!!.length)
        }

        binding.hideBtn2.setOnClickListener {
            when(it.tag) {
                "0" -> {
                    binding.hideBtn2.setTag("1")
                    binding.checkPwEdittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.hideBtn2.setBackgroundResource(R.drawable.ic_eye_on)
                }
                "1" -> {
                    binding.hideBtn2.setTag("0")
                    binding.checkPwEdittext.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.hideBtn2.setBackgroundResource(R.drawable.ic_eye_off)
                }
            }
            binding.checkPwEdittext.setSelection(binding.checkPwEdittext.text!!.length)
        }

        binding.pwEdittext.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.pwEdittext.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.pwEdittext.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.checkPwEdittext.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.checkPwEdittext.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.checkPwEdittext.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.pwEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkRequirements()
            }
        })

        binding.checkPwEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().equals(binding.pwEdittext.text.toString()) ){
                    binding.checkPwTextview.setTextColor(green)
                    binding.checkPwTextview.getCompoundDrawables()[0].setTint(green)
                    binding.nextBtn.isEnabled = true
                } else {
                    binding.checkPwTextview.setTextColor(gray)
                    binding.checkPwTextview.getCompoundDrawables()[0].setTint(gray)
                    binding.nextBtn.isEnabled = false
                }
            }
        })
    }

    fun setObserve() {
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it) {
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun checkRequirements() {
        binding.apply{
            var password = pwEdittext.text.toString().trim()
            val len = pwEdittext.text.toString().length
            val isProperLength  = if(len in 8..30)  true else false
            //false : 특수문자 o true 특수문자 x
            val notContainsSpecial = password.matches(spe_regex)
            val containsEng = eng_pattern.matcher(password).find()
            val containsNum = num_pattern.matcher(password).find()

            if(isProperLength) {
                checkLenghTextView.setTextColor(green)
                checkLenghTextView.getCompoundDrawables()[0].setTint(green)
            } else {
                checkLenghTextView.setTextColor(gray)
                checkLenghTextView.getCompoundDrawables()[0].setTint(gray)
            }

            if(containsEng) {
                checkEngTextView.setTextColor(green)
                checkEngTextView.getCompoundDrawables()[0].setTint(green)
            } else {
                checkEngTextView.setTextColor(gray)
                checkEngTextView.getCompoundDrawables()[0].setTint(gray)
            }

            if(!notContainsSpecial) {
                checkSpecTextView.setTextColor(green)
                checkSpecTextView.getCompoundDrawables()[0].setTint(green)
            } else {
                checkSpecTextView.setTextColor(gray)
                checkSpecTextView.getCompoundDrawables()[0].setTint(gray)
            }

            if(containsNum) {
                checkNumTextView.setTextColor(green)
                checkNumTextView.getCompoundDrawables()[0].setTint(green)
            } else {
                checkNumTextView.setTextColor(gray)
                checkNumTextView.getCompoundDrawables()[0].setTint(gray)
            }

            if(isProperLength && containsEng && !notContainsSpecial && containsNum)
                checkLayout.visibility = View.VISIBLE
            else
                checkLayout.visibility = View.GONE
        }
    }
}