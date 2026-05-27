package com.photi.aos.view.fragment.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.kakao.sdk.user.UserApiClient
import com.photi.aos.R
import com.photi.aos.data.enum.OAuthProvider
import com.photi.aos.databinding.FragmentUnSubscribeBinding
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.view.ui.util.KeyboardListener
import com.photi.aos.view.ui.util.LoadingDialogManager
import com.photi.aos.view.ui.util.OnKeyboardVisibilityListener
import com.photi.aos.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class UnSubscribeFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentUnSubscribeBinding
    private lateinit var mContext: Context
    private val settingsViewmodel by activityViewModels<SettingsViewModel>()
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_subscribe, container, false)
        binding.fragment = this
        binding.viewModel = settingsViewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar(" ")

        setListener()
        setObserve()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener(){
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (!visible)
                    binding.passwordEditText.clearFocus()
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isEmpty()) binding.nextButton.isEnabled = false
                else{
                    binding.nextButton.isEnabled = true
                    password = s.toString()
                }
            }
        })
    }

    fun showInputForm() {
        if (settingsViewmodel.isOAuthUser()) {
            LoadingDialogManager.show(requireActivity())
            when (settingsViewmodel.getOAuthProvider()) {
                OAuthProvider.KAKAO -> revokeKakaoThenWithdraw()
                OAuthProvider.GOOGLE -> revokeGoogleThenWithdraw()
                null -> LoadingDialogManager.hide()
            }
        } else {
            binding.textView.visibility = View.GONE
            binding.describeTextView.visibility = View.GONE
            binding.keepButton.visibility = View.GONE
            binding.cancleButton.visibility = View.GONE

            binding.enterPasswordLayout.visibility = View.VISIBLE
            binding.nextButtonLayout.visibility = View.VISIBLE
        }
    }

    private fun revokeKakaoThenWithdraw() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("UnSubscribe", "Kakao revoke FAIL", error)
                LoadingDialogManager.hide()
                CustomToast.createToast(activity, "잠시후 다시 시도해주세요.", "circle")?.show()
            } else {
                Log.d("UnSubscribe", "Kakao revoke SUCCESS")
                settingsViewmodel.withdrawOauth()
            }
        }
    }

    private fun revokeGoogleThenWithdraw() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(requireContext())
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                Log.d("UnSubscribe", "Google revoke SUCCESS")
                settingsViewmodel.withdrawOauth()
            } catch (e: Exception) {
                Log.e("UnSubscribe", "Google revoke FAIL", e)
                LoadingDialogManager.hide()
                CustomToast.createToast(activity, "잠시후 다시 시도해주세요.", "circle")?.show()
            }
        }
    }

    fun changeInputType(n : Int) {
        val imageButton = binding.hideBtn
        val editText = binding.passwordEditText

        if (editText.inputType == 0x00000091) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    fun goBack(){
        view?.findNavController()?.popBackStack()
    }


    fun setObserve() {
        settingsViewmodel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            LoadingDialogManager.hide()
            when (response.code) {
                "200 OK" -> {
                    val mActivity = activity as SettingsActivity
                    mActivity.unsubscribe()
                }

                "LOGIN_UNAUTHENTICATED" -> {
                    CustomOneButtonDialog(this,"비밀번호가 일치하지 않아요.","다시 입력해 주세요.","알겠어요")
                        .show(activity?.supportFragmentManager!!, "CustomDialog")
                }

                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }

                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(activity, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }

                "EXPIRED_TOKEN" -> {
                    CustomToast.createToast(activity, "만료된 토큰입니다.")?.show()
                }

                "INVALID_TOKEN" -> {
                    CustomToast.createToast(activity, "유효하지 않은 토큰입니다.")?.show()
                }

                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    override fun onClickYesButton() {
        binding.passwordEditText.setText("")
    }

    fun deleteUser() {
        LoadingDialogManager.show(requireActivity())
        settingsViewmodel.deleteUser()
    }
}