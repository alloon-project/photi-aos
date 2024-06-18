package com.example.alloon_aos.view.fragment.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentReportPageBinding
import com.example.alloon_aos.view.activity.ReportActivity

class ReportPageFragment : Fragment() {

    private lateinit var binding : FragmentReportPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_page, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as ReportActivity
        mActivity.setAppBar("신고하기")

        return binding.root
    }
    fun moveFrag(fragNum : Int){
        val ft = view?.findNavController()
        when(fragNum){
            1 -> {
                ft?.navigate(R.id.action_reportPageFragment_to_reportFeedFragment)
            }
            2 -> {
                ft?.navigate(R.id.action_reportPageFragment_to_reportMemberFragment)
            }
            3 ->{
                ft?.navigate(R.id.action_reportPageFragment_to_reportMissionFragment)
            }
        }
    }
}