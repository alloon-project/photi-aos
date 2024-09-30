package com.example.alloon_aos.view.fragment.create

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
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateContentFragment : Fragment() {
    private lateinit var binding : FragmentCreateContentBinding
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

        return binding.root
    }
}