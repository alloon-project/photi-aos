package com.photi.aos.view.fragment.auth

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.photi.aos.R
import com.photi.aos.databinding.FragmentLoginBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.view.ui.util.LoadingButtonManager.hideLoading
import com.photi.aos.view.ui.util.LoadingButtonManager.showLoading
import com.photi.aos.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mActivity: AuthActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        mActivity = activity as AuthActivity
        mActivity.setAppBar("로그인")

        //token 초기화
//        MyApplication.mySharedPreferences.remove("access_token")
//        MyApplication.mySharedPreferences.remove("refresh_token")
        
        authViewModel.resetAllValue()
        setObserve()
        setListener()

        return binding.root
    }


    fun onClickLogin() {
        if (binding.idEdittext.text.isEmpty() || binding.pwEdittext.text.isEmpty())
            CustomToast.createToast(mActivity,"아이디와 비밀번호 모두 입력해주세요")?.show()
        else {
            binding.loginBtn.showLoading()
            authViewModel.login()
        }
    }


    fun onClickKakaoLogin() {
        val act = activity ?: return

        launchKakaoLogin(
            activity = act,
            onSuccess = { idToken ->
                // TODO 서버 연동전
                Log.d("KakaoLogin", "SUCCESS idToken=$idToken")
                CustomToast.createToast(mActivity, "카카오 로그인 성공(임시)", "circle")?.show()
            },
            onFailure = { t ->
                Log.e("KakaoLogin", "FAIL", t)
                 if (t is ClientError && t.reason == ClientErrorCause.Cancelled) return@launchKakaoLogin
                CustomToast.createToast(mActivity, "카카오 로그인에 실패했어요. 다시 시도해주세요.", "circle")?.show()
            }
        )
    }


    fun onClickGoogleLogin() {
    }



    fun moveFrag(fragNum : Int){
        /*
        * 1 : login to signUp
        * 2 : login to findId
        * 3 : login to findPassword
        * */

        val ft = view?.findNavController()
        when(fragNum){
            1 -> {
                authViewModel.resetAllValue()
                ft?.navigate(R.id.action_loginFragment_to_signupEmailFragment)
            }
            2 -> {
                ft?.navigate(R.id.action_loginFragment_to_findIdFragment)
            }
            3 ->{
                ft?.navigate(R.id.action_loginFragment_to_findPasswordFragment)
            }
        }
    }

    private   fun setListener(){
        binding.idEdittext.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.idEdittext.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.idEdittext.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.idLinearlayout.isVisible = false
            }

        binding.pwEdittext.onFocusChangeListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    binding.pwEdittext.setBackgroundResource(R.drawable.input_line_focus)
                } else {
                    binding.pwEdittext.setBackgroundResource(R.drawable.input_line_default)
                }
                binding.pwLinearlayout.isVisible = false
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

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

   private fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            binding.loginBtn.hideLoading()
            when (response.code) {
                "200 OK" -> {
                    mActivity.finishActivity()
                }

                "LOGIN_UNAUTHENTICATED", "DELETED_USER" -> {
                    binding.idEdittext.setBackgroundResource(R.drawable.input_line_error)
                    binding.pwEdittext.setBackgroundResource(R.drawable.input_line_error)
                    binding.idLinearlayout.isVisible = true
                    binding.pwLinearlayout.isVisible = true
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

private fun launchKakaoLogin(
    activity: Activity,
    onSuccess: (String) -> Unit,
    onFailure: (Throwable) -> Unit,
) {
    // 공통: 카카오계정 로그인 콜백
    val accountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onFailure(error)
        } else if (token == null) {
            onFailure(IllegalStateException("Kakao login token is null"))
        } else {
            val idToken = token.idToken
            if (idToken.isNullOrBlank()) {
                onFailure(IllegalStateException("Kakao login idToken is null or blank"))
            } else {
                onSuccess(idToken)
            }
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
        UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->

            if (error != null) {
                // 사용자가 취소한 경우(뒤로가기 등)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onFailure(error)
                    return@loginWithKakaoTalk
                }

                // 카카오톡 로그인 실패 → 카카오계정 로그인으로 fallback
                UserApiClient.instance.loginWithKakaoAccount(activity, callback = accountCallback)
                return@loginWithKakaoTalk
            }

            if (token == null) {
                onFailure(IllegalStateException("Kakao login token is null"))
                return@loginWithKakaoTalk
            }

            val idToken = token.idToken
            if (idToken.isNullOrBlank()) {
                onFailure(IllegalStateException("Kakao login idToken is null or blank"))
                return@loginWithKakaoTalk
            }

            onSuccess(idToken)
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(activity, callback = accountCallback)
    }
}