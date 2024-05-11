package com.example.alloon_aos.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentLoginBinding
import com.example.alloon_aos.viewmodel.AuthViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        var id = null
        var loginId = binding.loginId
        var loginPw = binding.loginPw

        binding.loginBtn.setOnClickListener { view: View ->
            authViewModel.getData()
        }

        loginId.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginId.setBackgroundResource(R.drawable.input_line_default)
                binding.noId.isVisible = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginId.setBackgroundResource(R.drawable.input_line_focus)
                binding.noId.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
                loginId.setBackgroundResource(R.drawable.input_line_error)
                binding.noId.isVisible = true
            }

        })

        binding.loginPw.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginPw.setBackgroundResource(R.drawable.input_line_default)
                binding.noPw.isVisible = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginPw.setBackgroundResource(R.drawable.input_line_focus)
                binding.noPw.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
                loginPw.setBackgroundResource(R.drawable.input_line_error)
                binding.noPw.isVisible = true
            }

        })

        binding.signUp.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}