package com.example.alloon_aos.view.fragment.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateCompleteBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.adapter.RuleHashAdapter
import com.example.alloon_aos.view.ui.component.dialog.RuleCardDialog
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateCompleteFragment : Fragment() {
    private lateinit var binding : FragmentCreateCompleteBinding
    private val createViewModel by activityViewModels<CreateViewModel>()
    private lateinit var mActivity: CreateActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_complete, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as CreateActivity
        mActivity.setTitle()

        CustomToast.createToast(activity,"완성된 챌린지를 확인해볼까요? 찰칵~")?.show()
//        binding.hashRecyclerview.adapter = RuleHashAdapter(joinViewModel)
//        binding.hashRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//        binding.hashRecyclerview.setHasFixedSize(true)

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        binding.allButton.setOnClickListener {
//            RuleCardDialog(joinViewModel)
//                .show(activity?.supportFragmentManager!!, "CustomDialog")
        }

        binding.completeBtn.setOnClickListener {

        }
    }
}