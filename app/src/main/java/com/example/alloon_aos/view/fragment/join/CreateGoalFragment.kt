package com.example.alloon_aos.view.fragment.join

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentCreateGoalBinding
import com.example.alloon_aos.view.activity.JoinActivity
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.JoinViewModel

class CreateGoalFragment : Fragment() {
    private lateinit var binding : FragmentCreateGoalBinding
    private lateinit var mContext : Context
    private val joinViewModel by activityViewModels<JoinViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_goal, container, false)
        binding.fragment = this
        binding.viewModel = joinViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as JoinActivity
        mActivity.setAppBar("title")

        setListener()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setListener() {
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.goalEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                else    binding.goalEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.goalEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/120")

                binding.nextBtn.setBackgroundResource(R.drawable.btn_round_primary)
                binding.nextBtn.setText(R.string.save)
                binding.nextBtn.setTextColor(mContext.getColor(R.color.white))
            }
        })
    }
}