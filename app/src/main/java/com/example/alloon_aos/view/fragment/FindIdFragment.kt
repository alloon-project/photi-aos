package com.example.alloon_aos.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFindIdBinding
import com.example.alloon_aos.view.activity.HomeActivity
import com.example.alloon_aos.view.activity.MainActivity
import com.example.alloon_aos.viewmodel.MainViewModel

class FindIdFragment : Fragment() {
    private lateinit var binding : FragmentFindIdBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_id, container, false)
        binding.fragment = this
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        val mActivity = activity as HomeActivity
        mActivity.setAppBar("아이디 찾기")

        //setObserve()

        mainViewModel.login()

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailValidation()
            }
        })
        

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setObserve()
        }

    fun setObserve(){
        mainViewModel.code.observe(viewLifecycleOwner){
            println("HI!!!!!!!!!!!!2 $it")
            if(!it.isEmpty()){ //결과값이 안 비어 있을 때
                println("HI!!!!!!!!!!!!")
            }
        }
    }

    fun checkEmailValidation():Boolean{
        var email =binding.emailEditText.text.toString().trim() //공백제거
        val pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches()) {
            binding.errorTextView.visibility = View.INVISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_default)
            binding.nextButton.isEnabled = true


            //if(가입된 이메일)
                //정보보냈음
            //else
                // binding.errorTextView.visibility = View.VISIBLE
                // binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
                // binding.errorTextView.text = resources.getString(R.string.emailerror2)
            return true
        } else {
            binding.errorTextView.visibility = View.VISIBLE
            binding.emailEditText.background = resources.getDrawable(R.drawable.input_line_error)
            binding.nextButton.isEnabled = false

            return false
        }
    }

}