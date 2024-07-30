package com.example.alloon_aos.view.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentPasswordChangeBinding
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel
import java.util.regex.Pattern


class PasswordChangeFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentPasswordChangeBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    private val num_pattern = Pattern.compile("[0-9]")
    private val eng_pattern = Pattern.compile("[a-zA-Z]")
    private val spe_regex = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|@\\-\\_\\.\\;\\·ㆍᆞᆢ•‥a·﹕]*".toRegex()
    private var blue  = 0
    private var grey = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_change, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 재설정")

        blue  = resources.getColor(R.color.blue400)
        grey = resources.getColor(R.color.gray400)
        authViewModel.resetCodeValue()
        setListener()
        setObserve()


        return binding.root
    }

    fun setListener(){
        binding.newPassword1EditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.newPassword1EditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.newPassword1EditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.newPassword2EditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.newPassword2EditText.background = resources.getDrawable(R.drawable.input_line_focus)
            else    binding.newPassword2EditText.background = resources.getDrawable(R.drawable.input_line_default)
        }

        binding.newPassword1EditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkRequirements()
            }
        })

        binding.newPassword2EditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().equals(binding.newPassword1EditText.text.toString()) ) {
                    binding.checkPw.setTextColor(blue)
                    binding.checkPw.getCompoundDrawables()[0].setTint(blue)
                    binding.nextButton.isEnabled = true
                } else {
                    binding.checkPw.setTextColor(grey)
                    binding.checkPw.getCompoundDrawables()[0].setTint(grey)
                    binding.nextButton.isEnabled = false
                }
            }
        })
    }

    fun setObserve(){
        authViewModel.code.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                when(it){
                    "PASSWORD_CHANGED" -> {
                        CustomOneButtonDialog(this,"비밀번호가 변경되었어요","새 비밀번호로 로그인 해주세요","확인")
                            .show(activity?.supportFragmentManager!!, "CustomDialog")
                    }
                    "PASSWORD_MATCH_INVALID" -> {
                        CustomToast.createToast(getActivity(),"비밀번호와 비밀번호 재입력이 동일하지 않습니다.")?.show()
                    }
                    "TOKEN_UNAUTHENTICATED" ->{
                        CustomToast.createToast(getActivity(),"승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                    }
                    "LOGIN_UNAUTHENTICATED" -> {
                        CustomToast.createToast(getActivity(),"아이디 또는 비밀번호가 틀렸습니다.")?.show()
                    }
                    "TOKEN_UNAUTHORIZED"->{
                        CustomToast.createToast(getActivity(),"권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                    }
                    "IO_Exception" ->{
                        CustomToast.createToast(getActivity(),"IO_Exception: 인터넷이나 서버 연결을 확인해주세요")?.show()
                    }
                }
            }
        }
    }

    fun checkRequirements(){
        binding.apply{
            var password = newPassword1EditText.text.toString().trim()
            val len = newPassword1EditText.text.toString().length
            val isProperLength  = if(len in 8..30)  true else false
            //false : 특수문자 o true 특수문자 x
            val notContainsSpecial = password.matches(spe_regex)
            val containsEng = eng_pattern.matcher(password).find()
            val containsNum = num_pattern.matcher(password).find()

            if(isProperLength)  {
                checkLenghTextView.setTextColor(blue)
                checkLenghTextView.getCompoundDrawables()[0].setTint(blue)
            } else {
                checkLenghTextView.setTextColor(grey)
                checkLenghTextView.getCompoundDrawables()[0].setTint(grey)
            }

            if(containsEng) {
                checkEngTextView.setTextColor(blue)
                checkEngTextView.getCompoundDrawables()[0].setTint(blue)
            } else {
                checkEngTextView.setTextColor(grey)
                checkEngTextView.getCompoundDrawables()[0].setTint(grey)
            }

            if(!notContainsSpecial) {
                checkSpecTextView.setTextColor(blue)
                checkSpecTextView.getCompoundDrawables()[0].setTint(blue)
            } else {
                checkSpecTextView.setTextColor(grey)
                checkSpecTextView.getCompoundDrawables()[0].setTint(grey)
            }

            if(containsNum) {
                checkNumTextView.setTextColor(blue)
                checkNumTextView.getCompoundDrawables()[0].setTint(blue)
            } else {
                checkNumTextView.setTextColor(grey)
                checkNumTextView.getCompoundDrawables()[0].setTint(grey)
            }

            if(isProperLength && containsEng && !notContainsSpecial && containsNum)
                checkLayout.visibility = View.VISIBLE
            else
                checkLayout.visibility = View.GONE
        }
    }

    fun changeInputType(n : Int) {
        val imageButton = if (n == 1) binding.hide1Btn else binding.hide2Btn
        val editText = if (n == 1) binding.newPassword1EditText else binding.newPassword2EditText

        if (editText.inputType == 0x00000091) {
            imageButton.background = resources.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = resources.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    override fun onClickYesButton() {
        view?.findNavController()?.navigate(R.id.action_passwordChangeFragment_to_loginFragment)
    }
}