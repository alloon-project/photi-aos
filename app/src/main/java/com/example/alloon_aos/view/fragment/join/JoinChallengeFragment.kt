package com.example.alloon_aos.view.fragment.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentJoinChallengeBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.JoinActivity
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.adapter.RuleHashAdapter
import com.example.alloon_aos.view.ui.component.dialog.PrivateCodeDialog
import com.example.alloon_aos.view.ui.component.dialog.PrivateCodeDialogInterface
import com.example.alloon_aos.view.ui.component.dialog.RuleCardDialog
import com.example.alloon_aos.view.ui.component.dialog.UploadCardDialog
import com.example.alloon_aos.viewmodel.JoinViewModel

class JoinChallengeFragment : PrivateCodeDialogInterface, Fragment() {
    private lateinit var binding : FragmentJoinChallengeBinding
    private val joinViewModel by activityViewModels<JoinViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)
    private lateinit var mActivity: JoinActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_challenge, container, false)
        binding.fragment = this
        binding.viewModel = joinViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as JoinActivity
        mActivity.setAppBar(" ")

        binding.hashRecyclerview.adapter = RuleHashAdapter(joinViewModel)
        binding.hashRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.hashRecyclerview.setHasFixedSize(true)

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        binding.allButton.setOnClickListener {
            RuleCardDialog(joinViewModel)
                .show(activity?.supportFragmentManager!!, "CustomDialog")
        }

        binding.joinBtn.setOnClickListener {
            PrivateCodeDialog(this)
                .show(activity?.supportFragmentManager!!, "CustomDialog")
//            mActivity.supportFragmentManager.beginTransaction()
//                .replace(R.id.frame_Layout, CreateGoalFragment())
//                .addToBackStack(null)
//                .commit()

        }
    }

    override fun onClickYesButton() {
        mActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.frame_Layout, CreateGoalFragment())
            .addToBackStack(null)
            .commit()
    }

}