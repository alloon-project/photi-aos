package com.example.alloon_aos.view.fragment.photi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.FeedHistoryContent
import com.example.alloon_aos.databinding.FragmentChallengeLatestBinding
import com.example.alloon_aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.example.alloon_aos.view.activity.ChallengeActivity
import com.example.alloon_aos.view.ui.component.dialog.FeedHistoryDialog
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChallengeLatestFragment : Fragment() {
    private lateinit var binding : FragmentChallengeLatestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var latestCardAdapter: LatestCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_latest, container, false)
        binding.fragment = this

        latestCardAdapter = LatestCardAdapter()
        binding.latestRecyclerView.adapter = latestCardAdapter
        binding.latestRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.latestRecyclerView.setHasFixedSize(true)

        photiViewModel.resetApiResponseValue()
        photiViewModel.resetLatestResponseValue()
        setObserver()

//        nestedScrollView = binding.scrollView
//        nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
//            if (scrollY == nestedScrollView.getChildAt(0).measuredHeight - v.measuredHeight){
//                //if (!photiViewModel.lastLatestPage)
//                    //photiViewModel.getChallengeLatest()
//            }
//        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //photiViewModel.getChallengeLatest()
        photiViewModel.fetchLatestChallenge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photiViewModel.clearLatestChallengeData()
    }

    private fun setObserver() {
//        photiViewModel.latestItemsListData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
//            latestCardAdapter.notifyDataSetChanged()
//        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photiViewModel.latestChallengeData.collectLatest { pagingData ->
                  //  Log.d("PagingSource","${pagingData}")
                    latestCardAdapter.submitData(pagingData)
                    Log.d("PagingSource", "Adapter item count: ${latestCardAdapter.snapshot().items.size}")
                }
            }
        }

//        lifecycleScope.launch {
//            latestCardAdapter.loadStateFlow.collectLatest { loadStates ->
//                Log.d("PagingSource", "Refresh: ${loadStates.refresh}")
//                Log.d("PagingSource", "Append: ${loadStates.append}")
//            }
//        }

        photiViewModel.latestResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
//                "200 OK" -> {
//                    photiViewModel.latestPage += 1
//                }
                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
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

    private fun startChallenge() {
        val intent = Intent(requireContext(), ChallengeActivity::class.java)
        intent.putExtra("IS_FROM_HOME",true)
        intent.putExtra("ID",photiViewModel.id)
        intent.putExtra("data", photiViewModel.getData())
        intent.putExtra("image", photiViewModel.imgFile)
        startActivity(intent)
    }


    class LatestCardAdapter : PagingDataAdapter<ChallengeData, LatestCardAdapter.ViewHolder>(DiffCallback()){

        inner class ViewHolder(var binding : ItemCardMissionSmallRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(data: ChallengeData) {
                Log.d("ChallengeLatestFragment" ," 생성 : ${data.name}")
                binding.titleTextView.text = data.name
                binding.dateTextView.text = data.endDate

                if(data.hashtags.isNotEmpty()) {
                    data.hashtags.forEachIndexed { index, hashtag ->
                        when (index) {
                            0 -> {
                                binding.chip1Btn.text = hashtag.hashtag
                                binding.chip1Btn.visibility = View.VISIBLE
                                binding.chip2Btn.visibility = View.GONE
                                binding.chip3Btn.visibility = View.GONE
                            }
                            1 -> {
                                binding.chip2Btn.text = hashtag.hashtag
                                binding.chip2Btn.visibility = View.VISIBLE
                                binding.chip3Btn.visibility = View.GONE
                            }
                            2 -> {
                                binding.chip3Btn.text = hashtag.hashtag
                                binding.chip3Btn.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                Glide
                    .with(binding.imgView.context)
                    .load(data.imageUrl)
                    .into(binding.imgView)

//                binding.root.setOnClickListener {
//                    photiViewModel.id = data.id
//                    photiViewModel.getChallenge()
//                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemCardMissionSmallRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            getItem(position)?.let { viewHolder.bind(it) }
        }

        //override fun getItemCount() = photiViewModel.latestItems.size

        class DiffCallback : DiffUtil.ItemCallback<ChallengeData>() {
            override fun areItemsTheSame(oldItem: ChallengeData, newItem: ChallengeData): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ChallengeData, newItem: ChallengeData): Boolean {
                return oldItem == newItem
            }
        }

    }
}