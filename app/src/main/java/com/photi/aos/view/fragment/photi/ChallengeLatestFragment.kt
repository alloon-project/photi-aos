package com.photi.aos.view.fragment.photi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.photi.aos.R
import com.photi.aos.data.model.response.ChallengeData
import com.photi.aos.databinding.FragmentChallengeLatestBinding
import com.photi.aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChallengeLatestFragment : Fragment() {
    private lateinit var binding : FragmentChallengeLatestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var latestCardAdapter: LatestCardAdapter
    private lateinit var mActivity: PhotiActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_latest, container, false)
        binding.fragment = this

        mActivity = activity as PhotiActivity

        latestCardAdapter = LatestCardAdapter(mActivity, photiViewModel)
        binding.latestRecyclerView.adapter = latestCardAdapter
        binding.latestRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.latestRecyclerView.setHasFixedSize(true)

        photiViewModel.resetApiResponseValue()
        setObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        photiViewModel.fetchLatestChallenge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photiViewModel.clearLatestChallengeData()
    }

    private fun setObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photiViewModel.latestChallengeData.collectLatest { pagingData ->
                    latestCardAdapter.submitData(pagingData)
                    Log.d("PagingSource", "Adapter item count: ${latestCardAdapter.snapshot().items.size}")
                }
            }
        }
    }


    class LatestCardAdapter(private val mActivity: PhotiActivity, private val photiViewModel: PhotiViewModel) : PagingDataAdapter<ChallengeData, LatestCardAdapter.ViewHolder>(DiffCallback()){

        inner class ViewHolder(var binding : ItemCardMissionSmallRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(data: ChallengeData) {
                binding.titleTextView.text = data.name
                val formattedEnd = runCatching {
                    LocalDate.parse(data.endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(DateTimeFormatter.ofPattern("~yyyy. M. d"))
                }.getOrDefault("")
                binding.dateTextView.text = formattedEnd

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

                binding.root.setOnClickListener {
                    photiViewModel.id = data.id
                    if (photiViewModel.checkUserInChallenge())
                        mActivity.startFeedActivity()
                    else
                        photiViewModel.getChallenge()
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemCardMissionSmallRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            getItem(position)?.let { viewHolder.bind(it) }
        }

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