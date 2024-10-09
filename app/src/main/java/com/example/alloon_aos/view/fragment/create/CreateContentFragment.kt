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
import com.example.alloon_aos.databinding.FragmentCreateContentBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.ui.component.bottomsheet.DateBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.DateBottomSheetInterface
import com.example.alloon_aos.view.ui.component.bottomsheet.TimeBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.TimeBottomSheetInterface
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.CreateViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.DecimalFormat

class CreateContentFragment : Fragment(), TimeBottomSheetInterface, DateBottomSheetInterface {
    private lateinit var binding : FragmentCreateContentBinding
    private lateinit var mContext: Context
    private val createViewModel by activityViewModels<CreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_content, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as CreateActivity
        mActivity.setAppBar()

        setTodayDate()
        setListener()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setTodayDate() {
        val date = CalendarDay.today()
        val df = DecimalFormat("00")
        val month = df.format(date.month)
        val day = df.format(date.day)
        binding.dateTextview.setText("${date.year}. $month. $day")
    }

    fun setListener() {
        binding.timeBtn.setOnClickListener {
            TimeBottomSheet(mContext, createViewModel, this)
                .show(activity?.supportFragmentManager!!, "bottomList")
        }

        binding.dateBtn.setOnClickListener {
            DateBottomSheet(mContext, createViewModel, this)
                .show(activity?.supportFragmentManager!!, "bottomList")
        }

        binding.nextBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_createContentFragment_to_createImageFragment)
        }

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.contentEdittext.background = mContext.getDrawable(R.drawable.textarea_line_focus)
                else    binding.contentEdittext.background = mContext.getDrawable(R.drawable.textarea_line_default)
            }
        })

        binding.contentEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.numTextview.setText("${s!!.length}/120")
            }
        })
    }

    override fun onClickSelectTimeButton(time: Int) {
        val df = DecimalFormat("00")
        val timestring = df.format(time)
        binding.timeEdittext.setText("$timestring : 00")
        binding.timeImageview.setImageResource(R.drawable.ic_time_blue400)
        binding.timeTextview.setTextColor(mContext.getColor(R.color.blue400))
    }

    override fun onClickSelectDateButton(date: CalendarDay) {
        if (date != CalendarDay.today()) {
            val df = DecimalFormat("00")
            val month = df.format(date.month)
            val day = df.format(date.day)
            binding.selectedDateEdittext.setText("${date.year}. $month. $day")
        }
    }
}