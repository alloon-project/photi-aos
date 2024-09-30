package com.example.alloon_aos.view.fragment.create

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
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateNameBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateNameFragment : Fragment() {
    private lateinit var binding : FragmentCreateNameBinding
    private lateinit var mContext: Context
    private val createViewModel by activityViewModels<CreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_name, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as CreateActivity
        mActivity.setAppBar()

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

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.nameEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                else    binding.nameEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.nameEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/16")
                binding.nextBtn.isEnabled = true
            }
        })

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_createNameFragment_to_createContentFragment)
        }

        binding.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.nameTextview3.setText(R.string.createname3)
                binding.nameImageview.setImageResource(R.drawable.ic_postit_blue)
            } else {
                binding.nameTextview3.setText(R.string.createname4)
                binding.nameImageview.setImageResource(R.drawable.ic_people_blue)
            }
        }

    }
}