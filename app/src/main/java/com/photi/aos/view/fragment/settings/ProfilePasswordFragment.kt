package com.photi.aos.view.fragment.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.photi.aos.R
import com.photi.aos.databinding.FragmentProfilePasswordBinding
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.AuthViewModel

class ProfilePasswordFragment : Fragment() {
    private lateinit var binding: FragmentProfilePasswordBinding
    private lateinit var mContext: Context

    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var isPasswordValid = false
    private var isPasswordMatching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_password, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar(" ")

        (activity as? SettingsActivity)?.setAppBar(" ")

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getBooleanExtra("isFromPasswordChangeFragment", false)
                    if (data == true)
                        findNavController().popBackStack()
                    result.data?.getBooleanExtra("isFromPasswordChangeFragment", false)?.let {
                        if (it) findNavController().popBackStack()
                    }
                }
            }
        setObserve()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeyboardListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->

            when (response.code) {
                "200 OK" -> {
                    CustomToast.createToast(activity, "비밀번호 변경이 완료됐어요")?.show()
                }

                "PASSWORD_DUPLICATE_INVALID" -> {
                    binding.passwordRuleLayout.visibility = View.GONE
                    binding.passwordMatchLayout.visibility = View.VISIBLE
                }

                "PASSWORD_MATCH_INVALID" -> {
                    // CustomToast.createToast(activity, "비밀번호와 비밀번호 재입력이 동일하지 않습니다.")?.show()
                }

                "LOGIN_UNAUTHENTICATED" -> {

                    binding.passwordErrorLayout.visibility = View.VISIBLE
                }

                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(activity, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")
                        ?.show() //로그아웃 추가
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

    fun changeInputType(fieldIndex: Int) {
        var (imageButton, editText) = when (fieldIndex) {
            1 -> binding.hideBtn1 to binding.passwordEditText
            2 -> binding.hideBtn2 to binding.newPasswordEditText
            3 -> binding.hideBtn3 to binding.newPasswordEditText2
            else -> return
        }

        if (editText.inputType == 0x00000091) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    fun moveToAuth() {
        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra("IS_FROM_SETTINGS_ACTIVITY", true)
        }
        startForResult.launch(intent)
    }

    fun onNewPasswordChanged(password: CharSequence) {
        isPasswordValid = validatePassword(password.toString())
        updateValidationFeedback(password.toString())
        updateNextButtonState()

        binding.passwordRuleLayout.visibility = View.VISIBLE
        binding.passwordMatchLayout.visibility = View.GONE
        binding.passwordErrorLayout.visibility = View.GONE
    }

    fun onConfirmPasswordChanged(confirmPassword: CharSequence) {
        isPasswordMatching =
            confirmPassword.toString() == binding.newPasswordEditText.text.toString()
        updatePasswordMatchFeedback()
        updateNextButtonState()

        binding.passwordRuleLayout.visibility = View.VISIBLE
        binding.passwordMatchLayout.visibility = View.GONE
        binding.passwordErrorLayout.visibility = View.GONE
    }

    private fun validatePassword(password: String): Boolean {
        return password.length in 8..30 &&
                password.any { it.isLetter() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun updateValidationFeedback(password: String) {
        val conditions = listOf(
            Pair(
                password.any { it.isLetter() },
                binding.checkEngIconView to binding.checkEngTextView
            ),
            Pair(
                password.any { it.isDigit() },
                binding.checkNumIconView to binding.checkNumTextView
            ),
            Pair(
                password.any { !it.isLetterOrDigit() },
                binding.checkSpecIconView to binding.checkSpecTextView
            ),
            Pair(password.length in 8..30, binding.checkLenghIconView to binding.checkLenghTextView)
        )

        val blueColor = mContext.getColor(R.color.blue400)
        val greyColor = mContext.getColor(R.color.gray400)

        conditions.forEach { (isValid, views) ->
            val (iconView, textView) = views
            iconView.setImageResource(if (isValid) R.drawable.ic_check_blue else R.drawable.ic_check_grey)
            textView.setTextColor(if (isValid) blueColor else greyColor)
        }
    }

    private fun updatePasswordMatchFeedback() {
        val blueColor = mContext.getColor(R.color.blue400)
        val greyColor = mContext.getColor(R.color.gray400)
        val isValid = isPasswordMatching

        binding.checkPwIconView2.setImageResource(if (isValid) R.drawable.ic_check_blue else R.drawable.ic_check_grey)
        binding.checkPw2.setTextColor(if (isValid) blueColor else greyColor)
    }

    private fun updateNextButtonState() {
        binding.nextButton.isEnabled = isPasswordValid && isPasswordMatching
    }

    private fun setupKeyboardListener() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom

            if (keyboardHeight > screenHeight * 0.15) { // 키보드가 올라온 경우
                ensureViewIsAboveKeyboard(binding.checkPwLayout, keyboardHeight)
            } else {
                // 키보드가 내려간 경우 ScrollView를 원래대로 복구
                binding.scrollView.scrollTo(0, 0)
            }
        }
    }

    private fun ensureViewIsAboveKeyboard(view: View, keyboardHeight: Int) {
        binding.scrollView.post {
            val scrollBounds = Rect()
            binding.scrollView.getHitRect(scrollBounds)

            val viewBottom = view.bottom
            val keyboardTop = binding.root.height - keyboardHeight

            if (viewBottom > keyboardTop) {
                val scrollAmount = viewBottom - keyboardTop + 50 // 여유 공간 추가
                binding.scrollView.smoothScrollBy(0, scrollAmount)
            }
        }
    }
}