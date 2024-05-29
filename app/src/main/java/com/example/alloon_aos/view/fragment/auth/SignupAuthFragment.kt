package com.example.alloon_aos.view.fragment.auth

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentSignupAuthBinding
import com.example.alloon_aos.view.CustomToast
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.viewmodel.AuthViewModel

class SignupAuthFragment : Fragment() {
    private lateinit var binding : FragmentSignupAuthBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_auth, container, false)
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as AuthActivity
        mActivity.setAppBar("")

        authViewModel.resetCodeValue()
        setObserve()

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signupAuthFragment_to_signupIdFragment)
        }

        ObjectAnimator.ofInt(binding.authProgress, "progress", 20,40)
            .setDuration(500)
            .start()

        return binding.root
    }

    fun setObserve(){
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

    fun moveNext() {
        view?.findNavController()?.navigate(R.id.action_signupAuthFragment_to_signupIdFragment)
    }
}