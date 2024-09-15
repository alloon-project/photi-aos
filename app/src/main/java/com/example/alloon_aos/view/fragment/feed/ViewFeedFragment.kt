package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentViewFeedBinding
import com.example.alloon_aos.databinding.ItemFeedDefaultRecylcerviewBinding
import com.example.alloon_aos.databinding.ToastTooltipUnderBinding
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheetInterface
import com.example.alloon_aos.viewmodel.FeedViewModel
import com.google.android.material.snackbar.Snackbar

class ViewFeedFragment : Fragment(),AlignBottomSheetInterface {
    private lateinit var binding : FragmentViewFeedBinding
    private lateinit var mContext: Context
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var selected_order = "first"
    private var isMissionClear = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_feed, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.feedviewRecyclerView.adapter = FeedCardAdapter()
        binding.feedviewRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.feedviewRecyclerView.setHasFixedSize(true)


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isMissionClear) {
            showToast("DDDDD")
        }
    }
    fun showBottomList(){
        AlignBottomSheet(mContext,this,"최신순","인기순","작성순",selected_order)
            .show(activity?.supportFragmentManager!!, "bottomList")
    }

    private fun showToast(message:String){
        val inflater = LayoutInflater.from(requireContext())
        val customToastView = inflater.inflate(R.layout.toast_tooltip_under, null)

        // Set the message text and icon
        val messageTextView = customToastView.findViewById<TextView>(R.id.textView)

        messageTextView.text = message
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastView
        toast.show()
    }

    inner class ViewHolder(var binding : ItemFeedDefaultRecylcerviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun setContents(holder: ViewHolder ,pos: Int) {
            with (feedViewModel.feedItems[pos]) {
                binding.idTextView.text = id
                binding.timeTextView.text = time

                if(isClick){
                    binding.heartButton.setImageResource(R.drawable.ic_heart_filled)
                    binding.heartButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray700))
                }


               Glide
                    .with(holder.itemView.context)
                    .load(url)
                    .into(binding.imgView)
            }
        }
    }

    inner class FeedCardAdapter() : RecyclerView.Adapter<ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = ItemFeedDefaultRecylcerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(viewHolder,position)
        }

        override fun getItemCount() = feedViewModel.feedItems.size

    }

    override fun onClickFirstButton() {
        selected_order = "first"
        //최신순으로 정렬
    }

    override fun onClickSecondButton() {
        selected_order = "second"
        //인기순으로 정렬
    }

    override fun onClickThirdButton() {
        selected_order = "third"
        //작성순으로 정렬
    }

}