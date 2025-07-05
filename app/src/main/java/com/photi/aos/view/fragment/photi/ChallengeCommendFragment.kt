package com.photi.aos.view.fragment.photi

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photi.aos.R
import com.photi.aos.data.model.response.ChallengeData
import com.photi.aos.databinding.FragmentChallengeCommendBinding
import com.photi.aos.databinding.ItemCardMissionLargeRecyclerviewBinding
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.adapter.HashTagAdapter
import com.photi.aos.view.adapter.HotCardAdapter
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChallengeCommendFragment : Fragment() {
    private lateinit var binding : FragmentChallengeCommendBinding
    private lateinit var hotCardAdapter: HotCardAdapter
    private lateinit var hashCardAdapter: HashCardAdapter
    private lateinit var hashTagAdapter: HashTagAdapter

    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var mContext : Context
    private lateinit var mActivity: PhotiActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_commend, container, false)
        binding.fragment = this

        mActivity = activity as PhotiActivity

        hotCardAdapter = HotCardAdapter(this, photiViewModel)
        binding.hotRecyclerView.adapter = hotCardAdapter
        binding.hotRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.hotRecyclerView.setHasFixedSize(true)

        hashTagAdapter = HashTagAdapter(mContext,this, photiViewModel)
        binding.chipRecyclerview.adapter = hashTagAdapter
        binding.chipRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.chipRecyclerview.setHasFixedSize(true)

        hashCardAdapter = HashCardAdapter(this, photiViewModel)
        binding.hashtagRecyclerView.adapter = hashCardAdapter
        binding.hashtagRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.hashtagRecyclerView.setHasFixedSize(true)

        photiViewModel.resetApiResponseValue()
        photiViewModel.resetPopularResponseValue()
        photiViewModel.resetHashResponseValue()
        setObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        photiViewModel.getChallengePopular()
        photiViewModel.getHashList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photiViewModel.clearHashChallengeData()
    }


    private fun setObserver() {
        photiViewModel.hotItemsListData.observe(viewLifecycleOwner) {
            hotCardAdapter.notifyDataSetChanged()
        }

        photiViewModel.hashChipsListData.observe(viewLifecycleOwner) {
            hashTagAdapter.notifyDataSetChanged()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photiViewModel.hashChallengeData.collectLatest { pagingData ->
                    hashCardAdapter.submitData(pagingData)
                    Log.d("PagingSource", "Adapter item count: ${hashCardAdapter.snapshot().items.size}")
                }
            }
        }

        photiViewModel.popularResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                }
                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }
                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }

        photiViewModel.hashListResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    binding.allChipBtn.setBackgroundResource(R.drawable.chip_blue)
                    binding.allChipBtn.setTextColor(mContext.getColor(R.color.blue500))
                    photiViewModel.fetchChallengeHashtag("전체")
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
        if (photiViewModel.checkUserInChallenge())
            mActivity.startFeedActivity()
        else
            photiViewModel.getChallenge()
    }

    fun clickAllChip() {
        photiViewModel.clickAllChip()
        binding.allChipBtn.setBackgroundResource(R.drawable.chip_blue)
        binding.allChipBtn.setTextColor(mContext.getColor(R.color.blue500))
    }

    fun clickOneChip() {
        binding.allChipBtn.setBackgroundResource(R.drawable.chip_gray_line)
        binding.allChipBtn.setTextColor(mContext.getColor(R.color.gray800))
    }


    class HashCardAdapter(private val fragment: ChallengeCommendFragment , private val photiViewModel: PhotiViewModel) : PagingDataAdapter<ChallengeData, HashCardAdapter.ViewHolder> (DiffCallback()) {
        inner class ViewHolder(private val binding: ItemCardMissionLargeRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {

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
                    fragment.setOnclick()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemCardMissionLargeRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
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