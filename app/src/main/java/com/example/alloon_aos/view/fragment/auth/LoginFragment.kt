package com.example.alloon_aos.view.fragment.auth

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
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentLoginBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mActivity: AuthActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    fun setListener(){
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


    fun setObserve() {
        authViewModel.actionApiResponse.observe(viewLifecycleOwner) { response ->
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