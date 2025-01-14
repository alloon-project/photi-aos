package com.example.alloon_aos.view.fragment.photi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeCommendBinding
import com.example.alloon_aos.view.activity.ChallengeActivity
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.adapter.HashCardAdapter
import com.example.alloon_aos.view.adapter.HashTagAdapter
import com.example.alloon_aos.view.adapter.HotCardAdapter
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ChallengeCommendFragment : Fragment() {
    private lateinit var binding : FragmentChallengeCommendBinding
    private lateinit var hotCardAdapter: HotCardAdapter
    private lateinit var hashCardAdapter: HashCardAdapter
    private lateinit var hashTagAdapter: HashTagAdapter

    private val photiViewModel by activityViewModels<PhotiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_commend, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        hotCardAdapter = HotCardAdapter(this, photiViewModel)
        binding.hotRecyclerView.adapter = hotCardAdapter
        binding.hotRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.hotRecyclerView.setHasFixedSize(true)

        hashCardAdapter = HashCardAdapter(this, photiViewModel)
        binding.hashtagRecyclerView.adapter = hashCardAdapter
        binding.hashtagRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.hashtagRecyclerView.setHasFixedSize(true)

        hashTagAdapter = HashTagAdapter(photiViewModel)
        binding.chipRecyclerview.adapter = hashTagAdapter
        binding.chipRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.chipRecyclerview.setHasFixedSize(true)

        photiViewModel.resetApiResponseValue()
        setObserver()

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.hotItemsListData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            hotCardAdapter.notifyDataSetChanged()
        }

        photiViewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    startChallenge()
                }
                "TOKEN_UNAUTHENTICATED" -> {
                    CustomToast.createToast(activity, "승인되지 않은 요청입니다. 다시 로그인 해주세요.")?.show()
                }
                "TOKEN_UNAUTHORIZED" -> {
                    CustomToast.createToast(activity, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요.")?.show()
                }
                "CHALLENGE_NOT_FOUND" -> {
                    CustomToast.createToast(activity, "존재하지 않는 챌린지입니다.", "circle")?.show()
                }
                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    fun setOnclick() {
        photiViewModel.getChallengeInfo()
    }

    private fun startChallenge() {
        val intent = Intent(requireContext(), ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID",photiViewModel.id)
        intent.putExtra("data", photiViewModel.getData())
        intent.putExtra("image", photiViewModel.imgFile)
        startActivity(intent)
    }
}