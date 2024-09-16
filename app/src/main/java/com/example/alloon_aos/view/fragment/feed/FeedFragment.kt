package com.example.alloon_aos.view.fragment.feed

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentFeedBinding
import com.example.alloon_aos.view.adapter.FeedInAdapter
import com.example.alloon_aos.view.adapter.FeedOutAdapter
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheet
import com.example.alloon_aos.view.ui.component.bottomsheet.AlignBottomSheetInterface
import com.example.alloon_aos.view.ui.util.dpToPx
import com.example.alloon_aos.viewmodel.FeedViewModel

class FeedFragment : Fragment(),AlignBottomSheetInterface {
    private lateinit var binding : FragmentFeedBinding
    private lateinit var mContext: Context
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var feedInAdapter: FeedInAdapter
    private var selected_order = "first"
    private var isMissionClear = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        binding.fragment = this
        binding.viewModel = feedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.feedOutRecyclerView.adapter = FeedOutAdapter(feedViewModel)
        binding.feedOutRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedOutRecyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isMissionClear) {
            showToast("오늘의 인증이 완료되지 않았어요!")
        }
    }

    fun showBottomList(){
        AlignBottomSheet(mContext,this,"최신순","인기순","작성순",selected_order)
            .show(activity?.supportFragmentManager!!, "bottomList")
    }

    fun doTodayCertify(){
        Toast.makeText(requireContext(),"사진찍으러 ㄱㄱ",Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message:String){
        val inflater = LayoutInflater.from(requireContext())
        val customToastView = inflater.inflate(R.layout.toast_tooltip_under, null)

        customToastView.findViewById<TextView>(R.id.textView).text = message

        val toast = Toast(requireContext())
        val yOffset = requireContext().dpToPx(95f)

        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customToastView
        toast.show()
    }



    //피드들 정렬
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