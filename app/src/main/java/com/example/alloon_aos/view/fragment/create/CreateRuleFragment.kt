package com.example.alloon_aos.view.fragment.create

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateRuleBinding
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.adapter.CustomRuleAdapter
import com.example.alloon_aos.view.adapter.DefaultRuleAdapter
import com.example.alloon_aos.view.ui.component.bottomsheet.RuleBottomSheet
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateRuleFragment : Fragment(){
    private lateinit var binding : FragmentCreateRuleBinding
    private lateinit var mContext: Context
    private lateinit var defaultRuleAdapter: DefaultRuleAdapter
    private lateinit var customRuleAdapter: CustomRuleAdapter
    private val createViewModel by activityViewModels<CreateViewModel>()
    private lateinit var mActivity: CreateActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_rule, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as CreateActivity
        mActivity.setAppBar()

        if (mActivity.isFromChallenge)
            setModifyLayout()

        setListener()
        setObserver()

        defaultRuleAdapter = DefaultRuleAdapter(mContext, createViewModel,
            toastListener = { showToast() })
        binding.ruleRecyclerview.adapter = defaultRuleAdapter
        binding.ruleRecyclerview.layoutManager = GridLayoutManager(context, 2)
        binding.ruleRecyclerview.setHasFixedSize(true)

        customRuleAdapter = CustomRuleAdapter(mContext, createViewModel,
            onItemClickListener = { RuleBottomSheet(mContext,createViewModel)
                .show(activity?.supportFragmentManager!!, "CustomDialog") },
            toastListener = { showToast() }
        )

        binding.customRecyclerview.adapter = customRuleAdapter
        binding.customRecyclerview.layoutManager = GridLayoutManager(context, 2)
        binding.customRecyclerview.setHasFixedSize(true)

        ObjectAnimator.ofInt(binding.progress, "progress", 60,80)
            .setDuration(500)
            .start()

        return binding.root
    }

    private fun setModifyLayout() {
        mActivity.setTitle("챌린지 인증 룰 수정")
        binding.progress.visibility = View.GONE
        binding.nextBtn.setText("저장하기")
        binding.nextBtn.isEnabled = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setListener() {
        binding.nextBtn.setOnClickListener {
            createViewModel.setRuleList()
            if (mActivity.isFromChallenge)
                mActivity.modifyRule()
            else
                view?.findNavController()?.navigate(R.id.action_createRuleFragment_to_createHashtagFragment)
        }
    }

    private fun setObserver() {
        createViewModel.defaultRule.observe(viewLifecycleOwner) {
            defaultRuleAdapter.notifyDataSetChanged()

            binding.nextBtn.isEnabled = createViewModel.selectNum != 0
        }

        createViewModel.customRule.observe(viewLifecycleOwner) {
            customRuleAdapter.notifyDataSetChanged()

            binding.nextBtn.isEnabled = createViewModel.selectNum != 0
        }
    }

    fun showToast() {
        CustomToast.createToast(activity,"인증 룰은 5개까지 등록 가능해요")?.show()
    }
}