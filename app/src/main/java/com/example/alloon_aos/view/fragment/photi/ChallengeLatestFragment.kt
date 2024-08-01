package com.example.alloon_aos.view.fragment.photi

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentChallengeLatestBinding
import com.example.alloon_aos.databinding.ItemCardMissionSmallRecyclerviewBinding
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.adapter.HashCardAdapter
import com.example.alloon_aos.view.adapter.HotCardAdapter
import com.example.alloon_aos.viewmodel.Item
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ChallengeLatestFragment : Fragment() {
    private lateinit var binding : FragmentChallengeLatestBinding

    private val photiViewModel by activityViewModels<PhotiViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge_latest, container, false)
        binding.fragment = this
        val mActivity = activity as PhotiActivity

        binding.latestRecyclerView.adapter = LatestCardAdapter()
        binding.latestRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.latestRecyclerView.setHasFixedSize(true)

        return binding.root
    }


    inner class ViewHolder(var binding : ItemCardMissionSmallRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (photiViewModel.latestItems[pos]) {
                binding.titleTextView.text = title
                binding.dateTextView.text = date

                binding.chip1Btn.text = hashtag[0]
                binding.chip1Btn.visibility = View.VISIBLE
                if(hashtag.size == 2){
                    binding.chip2Btn.text = hashtag[1]
                    binding.chip2Btn.visibility = View.VISIBLE
                }


                val multiOption = MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(32),
                )

                Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(multiOption))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(a_resource: Drawable, a_transition: Transition<in Drawable>?) {
                            binding.cardLayout.background = a_resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
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