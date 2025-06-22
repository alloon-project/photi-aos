package com.photi.aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.photi.aos.R
import com.photi.aos.databinding.FragmentSignupPwBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.view.ui.component.bottomsheet.ListBottomSheet
import com.photi.aos.view.ui.component.bottomsheet.ListBottomSheetInterface
import com.photi.aos.viewmodel.AuthViewModel
import java.util.regex.Pattern

class SignupPwFragment : ListBottomSheetInterface,Fragment() {
    private lateinit var binding : FragmentSignupPwBinding
    private lateinit var mContext: Context
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mActivity: AuthActivity

    private val num_pattern = Pattern.compile("[0-9]")
    private val eng_pattern = Pattern.compile("[a-zA-Z]")
    private val spe_regex = "[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*".toRegex()

    private var blue  = 0
    private var gray = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_pw, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        blue  = mContext.getColor(R.color.blue400)
        gray = mContext.getColor(R.color.gray400)

        authViewModel.resetApiResponseValue()
        setObserve()
        setListener()

        ObjectAnimator.ofInt(binding.pwProgress, "progress", 60,80)
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
            if(hasFocus) binding.pwEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
            else    binding.pwEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
        }

        binding.checkPwEdittext.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.checkPwEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
            else    binding.checkPwEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
        }

        binding.pwEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkRequirements()
                binding.checkPwEdittext.setText("")
            }
        })

        binding.checkPwEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().equals(binding.pwEdittext.text.toString()) ){
                    binding.checkPwTextview.setTextColor(blue)
                    binding.checkPwIconview.setImageResource(R.drawable.ic_check_blue)
                    binding.nextBtn.isEnabled = true
                } else {
                    binding.checkPwTextview.setTextColor(gray)
                    binding.checkPwIconview.setImageResource(R.drawable.ic_check_grey)
                    binding.nextBtn.isEnabled = false
                }
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "201 CREATED" -> {
                    mActivity.finishActivity()
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
                checkLenghTextView.setTextColor(blue)
                checkLenghIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkLenghTextView.setTextColor(gray)
                checkLenghIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(containsEng) {
                checkEngTextView.setTextColor(blue)
                checkEngIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkEngTextView.setTextColor(gray)
                checkEngIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(!notContainsSpecial) {
                checkSpecTextView.setTextColor(blue)
                checkSpecIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkSpecTextView.setTextColor(gray)
                checkSpecIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(containsNum) {
                checkNumTextView.setTextColor(blue)
                checkNumIconView.setImageResource(R.drawable.ic_check_blue)
            } else {
                checkNumTextView.setTextColor(gray)
                checkNumIconView.setImageResource(R.drawable.ic_check_grey)
            }

            if(isProperLength && containsEng && !notContainsSpecial && containsNum)
                checkLayout.visibility = View.VISIBLE
            else
                checkLayout.visibility = View.GONE
        }
    }

    fun showBottomList(){
        ObjectAnimator.ofInt(binding.pwProgress, "progress", 80,100)
            .setDuration(500)
            .start()
        ListBottomSheet(this,"포티 서비스 이용을 위한\n" +
                "필수 약관에 동의해주세요","개인정보 수집 및 이용 동의","서비스 이용약관 동의","동의 후 계속")
            .show(activity?.supportFragmentManager!!, "CustomDialog")
    }

    override fun onClickImgButton1() {
        // 개인 정보 수집 및 이용 동의
        val notionPageUrl = "https://www.notion.so/16c7071e9b43802fac6beedbac719400"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notionPageUrl))
        startActivity(intent)
    }

    override fun onClickImgButton2() {
        // 서비스 이용 약관 동의
        val notionPageUrl = "https://www.notion.so/f1dc17026f884c2ebe90437b0ee9fa63"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notionPageUrl))
        startActivity(intent)
        CustomToast.createToast(activity,"두번째 약관 클릭")?.show()
    }

    override fun onClickButton() {
        authViewModel.signUp()
    }
}