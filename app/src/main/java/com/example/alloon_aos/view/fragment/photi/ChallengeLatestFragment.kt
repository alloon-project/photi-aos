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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeLatestBinding
import com.example.alloon_aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.example.alloon_aos.view.activity.ChallengeActivity
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ChallengeLatestFragment : Fragment() {
    private lateinit var binding : FragmentChallengeLatestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var latestCardAdapter: LatestCardAdapter
    private lateinit var nestedScrollView: NestedScrollView

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

        nestedScrollView = binding.scrollView
        nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == nestedScrollView.getChildAt(0).measuredHeight - v.measuredHeight){
                photiViewModel.getChallengeLatest()
            }
        }

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.latestItemsListData.observe(viewLifecycleOwner) { // 데이터에 변화가 있을 때 어댑터에게 변경을 알림
            latestCardAdapter.notifyDataSetChanged()
        }

        photiViewModel.latestResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    photiViewModel.latestPage += 10
                }
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

    inner class ViewHolder(var binding : ItemCardMissionSmallRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (photiViewModel.latestItems[pos]) {
                binding.titleTextView.text = name
                binding.dateTextView.text = endDate

                if(hashtags.isNotEmpty()) {
                    hashtags.forEachIndexed { index, hashtag ->
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
                    .with(holder.itemView.context)
                    .load(imageUrl)
                    .into(binding.imgView)

                binding.root.setOnClickListener {
                    photiViewModel.id = id
                    photiViewModel.getChallenge()
                }
            }
        }
    }

    inner class LatestCardAdapter() : RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemCardMissionSmallRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder,position)
        }

        override fun getItemCount() = photiViewModel.latestItems.size

    }
}