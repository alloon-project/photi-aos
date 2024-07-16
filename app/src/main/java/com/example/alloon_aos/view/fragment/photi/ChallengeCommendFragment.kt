package com.example.alloon_aos.view.fragment.photi

import android.os.Bundle
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
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.adapter.HashCardAdapter
import com.example.alloon_aos.view.adapter.HashTagAdapter
import com.example.alloon_aos.view.adapter.HotCardAdapter
import com.example.alloon_aos.viewmodel.Chip
import com.example.alloon_aos.viewmodel.Item
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

        hotCardAdapter = HotCardAdapter(photiViewModel)
        binding.hotRecyclerView.adapter = hotCardAdapter
        binding.hotRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.hotRecyclerView.setHasFixedSize(true)

        hashCardAdapter = HashCardAdapter(photiViewModel)
        binding.hashtagRecyclerView.adapter = hashCardAdapter
        binding.hashtagRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.hashtagRecyclerView.setHasFixedSize(true)

        hashTagAdapter = HashTagAdapter(photiViewModel)
        binding.chipRecyclerview.adapter = hashTagAdapter
        binding.chipRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.chipRecyclerview.setHasFixedSize(true)

        //setObserver()

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.hotItemsListData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            hotCardAdapter.notifyDataSetChanged() // 어댑터가 리사이클러뷰에게 알려 내용을 갱신함
        }
    }
}