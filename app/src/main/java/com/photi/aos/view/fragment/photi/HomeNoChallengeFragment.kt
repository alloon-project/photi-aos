package com.photi.aos.view.fragment.photi

import MemberHomeTransformer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.photi.aos.MyApplication
import com.photi.aos.R
import com.photi.aos.data.storage.SharedPreferencesManager
import com.photi.aos.databinding.FragmentHomeNochallengeBinding
import com.photi.aos.view.activity.CreateActivity
import com.photi.aos.view.activity.PhotiActivity
import com.photi.aos.view.adapter.MemberHomeAdapter
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.viewmodel.PhotiViewModel

class HomeNoChallengeFragment : Fragment() {
    private lateinit var binding : FragmentHomeNochallengeBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private lateinit var mActivity: PhotiActivity
    private lateinit var memberHomeAdapter: MemberHomeAdapter
    private val sharedPreferencesManager = SharedPreferencesManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_nochallenge, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as PhotiActivity

        binding.memberTextview.text = "${sharedPreferencesManager.getUserName() ?: ""}님의 \n열정을 보여주세요!"

        memberHomeAdapter = MemberHomeAdapter(photiViewModel)
        binding.viewPager.adapter = memberHomeAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.setPageTransformer(MemberHomeTransformer())

        setObserver()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        photiViewModel.getChallengePopular()
    }

    private fun setObserver() {
        photiViewModel.hotItemsListData.observe(viewLifecycleOwner){
            memberHomeAdapter.notifyDataSetChanged()
        }

        photiViewModel._photoItem.observe(viewLifecycleOwner){
            binding.titleTextview.setText(it.name)
            binding.contentTextview.setText(it.goal)
            binding.dateTextview.setText(it.endDate)
            binding.timeTextview.setText(it.proveTime)

            binding.hash1Btn.visibility = View.GONE
            binding.hash2Btn.visibility = View.GONE
            binding.hash3Btn.visibility = View.GONE

            it.hashtags.forEachIndexed { index, hashtag ->
                when (index) {
                    0 -> {
                        binding.hash1Btn.text = hashtag.hashtag
                        binding.hash1Btn.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.hash2Btn.text = hashtag.hashtag
                        binding.hash2Btn.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.hash3Btn.text = hashtag.hashtag
                        binding.hash3Btn.visibility = View.VISIBLE
                    }
                }
            }

            val cnt = it.currentMemberCnt
            val imgs = it.memberImages

            when (cnt) {
                1 -> {
                    binding.avatarOneLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "1명 도전 중"
                    loadImage(binding.oneUser1ImageView, imgs.getOrNull(0)?.memberImage)
                }
                2 -> {
                    binding.avatarTwoLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "2명 도전 중"
                    loadImage(binding.twoUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.twoUser2ImageView, imgs.getOrNull(1)?.memberImage)
                }
                3 -> {
                    binding.avatarThreeLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "3명 도전 중"
                    loadImage(binding.threeUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.threeUser2ImageView, imgs.getOrNull(1)?.memberImage)
                    loadImage(binding.threeUser3ImageView, imgs.getOrNull(2)?.memberImage)
                }
                else -> {
                    binding.avatarMultipleLayout.visibility = View.VISIBLE
                    binding.membernumTextview.text = "${cnt}명 도전 중"
                    loadImage(binding.multipleUser1ImageView, imgs.getOrNull(0)?.memberImage)
                    loadImage(binding.multipleUser2ImageView, imgs.getOrNull(1)?.memberImage)
                    binding.countTextView.text = "+${(cnt - 2).coerceAtLeast(0)}"
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == photiViewModel.hotItems.size) {
                    binding.photoLayout.visibility = View.GONE
                    binding.addLayout.visibility = View.VISIBLE
                } else {
                    val selectedPhoto = photiViewModel.hotItems.get(position)
                    if (selectedPhoto != null) {
                        photiViewModel.setCurrentPhoto(selectedPhoto)
                    }
                    binding.addLayout.visibility = View.GONE
                    binding.photoLayout.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun loadImage(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(url)
                .circleCrop()
                .into(imageView)
        }
    }

    fun joinChallenge() {
        photiViewModel.getChallenge()
    }

    fun createChallenge() {
        val intent = Intent(requireContext(), CreateActivity::class.java)
        startActivity(intent)
    }

}