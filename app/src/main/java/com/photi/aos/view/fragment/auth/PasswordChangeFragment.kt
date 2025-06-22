package com.photi.aos.view.fragment.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.photi.aos.R
import com.photi.aos.databinding.FragmentPasswordChangeBinding
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.viewmodel.AuthViewModel
import java.util.regex.Pattern


class PasswordChangeFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentPasswordChangeBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var isFromSettingsActivity = false
    private lateinit var mContext : Context
    private lateinit var mActivity: AuthActivity

    private val num_pattern = Pattern.compile("[0-9]")
    private val eng_pattern = Pattern.compile("[a-zA-Z]")
    private val spe_regex = "[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*".toRegex()
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

        mActivity = activity as AuthActivity
        mActivity.setAppBar("비밀번호 재설정")

        isFromSettingsActivity = mActivity.isFromSettingsActivity

        blue  = mContext.getColor(R.color.blue400)
        grey = mContext.getColor(R.color.gray400)
        authViewModel.resetApiResponseValue()
        setListener()
        setObserve()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener(){
        binding.newPassword1EditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.newPassword1EditText.background = mContext.getDrawable(R.drawable.input_line_focus)
            else    binding.newPassword1EditText.background = mContext.getDrawable(R.drawable.input_line_default)
        }

        binding.newPassword2EditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.newPassword2EditText.background = mContext.getDrawable(R.drawable.input_line_focus)
            else    binding.newPassword2EditText.background = mContext.getDrawable(R.drawable.input_line_default)
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
                    binding.checkPwIconView.setImageResource(R.drawable.ic_check_blue)
                    binding.nextButton.isEnabled = true
                } else {
                    binding.checkPw.setTextColor(grey)
                    binding.checkPwIconView.setImageResource(R.drawable.ic_check_grey)
                    binding.nextButton.isEnabled = false
                }
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    CustomOneButtonDialog(
                        this,
                        "비밀번호가 변경되었어요",
                        "새 비밀번호로 로그인 해주세요",
                        "확인"
                    ).show(activity?.supportFragmentManager!!, "CustomDialog")
                }

                "PASSWORD_MATCH_INVALID" -> {
                    CustomToast.createToast(activity, "비밀번호와 비밀번호 재입력이 동일하지 않습니다.")?.show()
                }

                "LOGIN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(activity, "아이디 또는 비밀번호가 틀렸습니다.")?.show()
                }

                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(activity, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
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
                checkLenghIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkLenghTextView.setTextColor(grey)
                checkLenghIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(containsEng) {
                checkEngTextView.setTextColor(blue)
                checkEngIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkEngTextView.setTextColor(grey)
                checkEngIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(!notContainsSpecial) {
                checkSpecTextView.setTextColor(blue)
                checkSpecIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkSpecTextView.setTextColor(grey)
                checkSpecIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(containsNum) {
                checkNumTextView.setTextColor(blue)
                checkNumIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkNumTextView.setTextColor(grey)
                checkNumIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(isProperLength && containsEng && !notContainsSpecial && containsNum)
                checkLayout.visibility = View.VISIBLE
            else {
                binding.checkLayout.visibility = View.GONE
                binding.newPassword2EditText.text.clear()
            }
        }
    }

    fun changeInputType(n : Int) {
        val imageButton = if (n == 1) binding.hide1Btn else binding.hide2Btn
        val editText = if (n == 1) binding.newPassword1EditText else binding.newPassword2EditText

        if (editText.inputType == 0x00000091) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    override fun onClickYesButton() {
        if(isFromSettingsActivity){
            mActivity.finishActivity()
        }

        view?.findNavController()?.navigate(R.id.action_passwordChangeFragment_to_loginFragment)
    }
}