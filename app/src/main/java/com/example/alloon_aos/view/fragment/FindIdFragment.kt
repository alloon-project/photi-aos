package com.example.alloon_aos.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindIdBinding
import com.example.alloon_aos.view.activity.HomeActivity
import com.example.alloon_aos.view.activity.MainActivity
import com.example.alloon_aos.viewmodel.MainViewModel

class FindIdFragment : Fragment() {
    private lateinit var binding : FragmentFindIdBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        binding.fragment = this

        val mActivity = activity as HomeActivity
        mActivity.setAppBar("아이디 찾기")


        // addTextChangedListener의 경우 익명클래스이니 필수 함수들을 import 해줘야 함
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailValidation()
            }
        })

        return binding.root
    }

    fun checkEmailValidation():Boolean{
        var email =binding.emailEditText.text.toString().trim() //공백제거
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.errorTextView.visibility = View.INVISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            //아이디찾기API
            //mainViewModel.findId("ejsong428@gmail.com")
            //if(가입된 이메일)
                //버튼 활성화
            //else
                // binding.errorTextView.visibility = View.VISIBLE
                // binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
                // binding.errorTextView.text = resources.getString(R.string.emailerror2)
            return true
        } else {
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)


            return false
        }
    }


}