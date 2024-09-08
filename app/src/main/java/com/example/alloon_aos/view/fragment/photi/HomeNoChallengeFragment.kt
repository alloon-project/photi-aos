package com.example.alloon_aos.view.fragment.photi

import MemberHomeTransformer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentHomeNochallengeBinding
import com.example.alloon_aos.view.activity.FeedActivity
import com.example.alloon_aos.view.adapter.MemberHomeAdapter
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeNoChallengeFragment : Fragment() {
    private lateinit var binding : FragmentHomeNochallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var memberHomeAdapter: MemberHomeAdapter
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_nochallenge, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        memberHomeAdapter = MemberHomeAdapter(photiViewModel, emptyList())
        binding.viewPager.adapter = memberHomeAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.setPageTransformer(MemberHomeTransformer())

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        photiViewModel.photoItemList.observe(viewLifecycleOwner){
            memberHomeAdapter.updatePhotoItems(it)
        }

        photiViewModel._photoItem.observe(viewLifecycleOwner){
            binding.titleTextview.setText(it.title)
            binding.contentTextview.setText(it.content)
            binding.dateTextview.setText(it.data)
            binding.timeTextview.setText(it.time)
            binding.membernumTextview.setText(it.member)

            binding.hash1Btn.visibility = View.GONE
            binding.hash2Btn.visibility = View.GONE
            binding.hash3Btn.visibility = View.GONE

            it.hashtag.forEachIndexed { index, hashtag ->
                when (index) {
                    0 -> {
                        binding.hash1Btn.text = hashtag
                        binding.hash1Btn.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.hash2Btn.text = hashtag
                        binding.hash2Btn.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.hash3Btn.text = hashtag
                        binding.hash3Btn.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == photiViewModel.photoItems.size) {
                    binding.photoLayout.visibility = View.GONE
                    binding.addLayout.visibility = View.VISIBLE
                } else {
                    val selectedPhoto = photiViewModel.photoItems.get(position)
                    if (selectedPhoto != null) {
                        photiViewModel.setCurrentPhoto(selectedPhoto)
                    }
                    binding.addLayout.visibility = View.GONE
                    binding.photoLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    fun moveToFeedActivity(){
        startActivity(Intent(activity, FeedActivity::class.java))
    }

}