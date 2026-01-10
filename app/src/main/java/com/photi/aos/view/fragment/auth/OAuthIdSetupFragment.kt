package com.photi.aos.view.fragment.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.photi.aos.R
import com.photi.aos.databinding.BottomsheetListBinding
import com.photi.aos.databinding.FragmentOAuthIdSetupBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.view.ui.component.bottomsheet.ListBottomSheet
import com.photi.aos.view.ui.component.bottomsheet.ListBottomSheetInterface
import com.photi.aos.view.ui.util.LoadingButtonManager.hideLoading
import com.photi.aos.view.ui.util.LoadingButtonManager.showLoading
import com.photi.aos.viewmodel.AuthViewModel

class OAuthIdSetupFragment :ListBottomSheetInterface, Fragment() {
    private lateinit var binding : FragmentOAuthIdSetupBinding
    private lateinit var bottomsheetListBinding: BottomsheetListBinding
    private lateinit var mContext : Context
    private lateinit var mActivity: AuthActivity
    private val authViewModel by activityViewModels<AuthViewModel>()

    private var blue  = 0
    private var red = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_o_auth_id_setup, container, false)
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
            showPolicyBottomList()
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible) {
                    setFocusMode()
                } else {
                    binding.idEdittext.clearFocus()
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
                setFocusMode()
                if (s!!.length < 5)
                    binding.checkBtn.isEnabled = false
                else
                    binding.checkBtn.isEnabled = true
            }
        })
    }

    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            if (::bottomsheetListBinding.isInitialized)
                bottomsheetListBinding.btn.hideLoading()

            when (response.code) {
                "200 OK" -> {
                    binding.idLinearlayout.isVisible = true
                    binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                    binding.idErrorTextview.text = "사용할 수 있는 아이디예요"
                    binding.idErrorTextview.setTextColor(blue)
                    binding.idIconView.setImageResource(R.drawable.ic_check_blue)
                    binding.nextBtn.isEnabled = true
                }

                "OAUTH_LOGIN_SUCCESS" -> {
                    mActivity.finishActivity()
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

    private fun setFocusMode() {
        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
        binding.idLinearlayout.isVisible = false
    }

    private fun setErrorMsg() {
        binding.idLinearlayout.isVisible = true
        binding.idEdittext.background = mContext.getDrawable(R.drawable.input_line_error)
        binding.nextBtn.isEnabled = false
        binding.checkBtn.isEnabled = false
        binding.idErrorTextview.setTextColor(red)
        binding.idIconView.setImageResource(R.drawable.ic_close_default)
    }

   private fun showPolicyBottomList(){
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
    }

    override fun onClickButton(binding: BottomsheetListBinding) {
        this@OAuthIdSetupFragment.bottomsheetListBinding = binding
        this@OAuthIdSetupFragment.bottomsheetListBinding.btn.showLoading()
        authViewModel.updateOAuthUserName(this.binding.idEdittext.toString())
    }

}