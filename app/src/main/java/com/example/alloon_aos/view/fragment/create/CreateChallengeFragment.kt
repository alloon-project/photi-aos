package com.example.alloon_aos.view.fragment.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateChallengeBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateChallengeFragment : Fragment() {
    private lateinit var binding : FragmentCreateChallengeBinding
    private lateinit var mContext: Context
    private val createViewModel by activityViewModels<CreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_challenge, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mActivity = activity as CreateActivity
        mActivity.setAppBar()

        binding.startBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_createChallengeFragment_to_createNameFragment)
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}