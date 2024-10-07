package com.example.alloon_aos.view.fragment.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateContentBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.ui.component.bottomsheet.TimeBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.TimeBottomSheetInterface
import com.example.alloon_aos.viewmodel.CreateViewModel
import java.text.DecimalFormat

class CreateContentFragment : Fragment(), TimeBottomSheetInterface {
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

        setListener()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener() {
        binding.downBtn.setOnClickListener {
            TimeBottomSheet(mContext,createViewModel,this)
                .show(activity?.supportFragmentManager!!, "bottomList")
        }
    }

    override fun onClickSelectButton(time: Int) {
        val df = DecimalFormat("00")
        val timetext = df.format(time)
        binding.timeEdittext.setText("$timetext : 00")
        binding.timeImageview.setImageResource(R.drawable.ic_time_blue400)
        binding.timeTextview.setTextColor(mContext.getColor(R.color.blue400))
    }
}