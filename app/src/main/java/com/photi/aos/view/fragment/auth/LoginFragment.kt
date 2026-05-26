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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.photi.aos.BuildConfig
import com.photi.aos.R
import com.photi.aos.data.enum.OAuthProvider
import com.photi.aos.databinding.FragmentLoginBinding
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.AuthActivity
import com.photi.aos.view.ui.util.LoadingButtonManager.hideLoading
import com.photi.aos.view.ui.util.LoadingButtonManager.showLoading
import com.photi.aos.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.SecureRandom

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
            onSuccess = { idToken, sub ->
-                Log.d("KakaoLogin", "idToken: $idToken")
                authViewModel.loginOauth(OAuthProvider.KAKAO, idToken, sub)
            },
            onFailure = { t ->
                Log.e("KakaoLogin", "FAIL", t)
                 if (t is ClientError && t.reason == ClientErrorCause.Cancelled) return@launchKakaoLogin
            }
        )
    }


    fun onClickGoogleLogin() {
        val act = activity ?: return

        val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

        launchGoogleLogin(
            activity = act,
            webClientId = webClientId,
            scope = viewLifecycleOwner.lifecycleScope,
            onSuccess = { idToken, sub ->
                Log.d("GoogleLogin", "idToken: $idToken")
                authViewModel.loginOauth(OAuthProvider.GOOGLE, idToken, sub)
            },
            onFailure = { t ->
                Log.e("GoogleLogin", "FAIL", t)
                if (t is GetCredentialCancellationException) return@launchGoogleLogin
            }
        )

    }



    fun moveFrag(fragNum : Int){
        /*
        * 1 : login to signUp
        * 2 : login to findId
        * 3 : login to findPassword
        * 4 : login to signUpId(OAuth)
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
            4 ->{
                ft?.navigate(R.id.action_loginFragment_to_oAuthIdSetupFragment)
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

                "OAUTH_NEED_NICKNAME" -> {
                    moveFrag(4)
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
    onSuccess: (idToken: String, sub: String) -> Unit,
    onFailure: (Throwable) -> Unit,
) {
    val accountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onFailure(error)
        } else if (token == null) {
            onFailure(IllegalStateException("Kakao login token is null"))
        } else {
            val idToken = token.idToken
            val sub = idToken?.let { extractSubFromJwt(it) }
            if (idToken.isNullOrBlank() || sub.isNullOrBlank()) {
                onFailure(IllegalStateException("Kakao login idToken or sub is null or blank"))
            } else {
                onSuccess(idToken, sub)
            }
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
        UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->

            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onFailure(error)
                    return@loginWithKakaoTalk
                }
                UserApiClient.instance.loginWithKakaoAccount(activity, callback = accountCallback)
                return@loginWithKakaoTalk
            }

            if (token == null) {
                onFailure(IllegalStateException("Kakao login token is null"))
                return@loginWithKakaoTalk
            }

            val idToken = token.idToken
            val sub = idToken?.let { extractSubFromJwt(it) }
            if (idToken.isNullOrBlank() || sub.isNullOrBlank()) {
                onFailure(IllegalStateException("Kakao login idToken or sub is null or blank"))
                return@loginWithKakaoTalk
            }

            onSuccess(idToken, sub)
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(activity, callback = accountCallback)
    }
}


private fun launchGoogleLogin(
    activity: Activity,
    webClientId: String,
    scope: CoroutineScope,
    onSuccess: (idToken: String, sub: String) -> Unit,
    onFailure: (Throwable) -> Unit,
) {
    val credentialManager = CredentialManager.create(activity)

    val googleOption = GetSignInWithGoogleOption.Builder(webClientId)
        .setNonce(generateSecureRandomNonce())
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity,
            )

            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken
            val sub = extractSubFromJwt(idToken)

            if (sub.isNullOrBlank()) {
                onFailure(IllegalStateException("Google login sub is null or blank"))
                return@launch
            }

            onSuccess(idToken, sub)

        } catch (e: NoCredentialException) {
            onFailure(e)
        } catch (e: GetCredentialException) {
            onFailure(e)
        } catch (t: Throwable) {
            onFailure(t)
        }
    }
}

private fun generateSecureRandomNonce(length: Int = 32): String {
    val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._"
    val random = SecureRandom()
    return buildString(length) {
        repeat(length) { append(charset[random.nextInt(charset.length)]) }
    }
}

private fun extractSubFromJwt(jwt: String): String? {
    return try {
        val payload = jwt.split(".").getOrNull(1) ?: return null
        val decoded = String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING))
        org.json.JSONObject(decoded).getString("sub")
    } catch (e: Exception) {
        null
    }
}