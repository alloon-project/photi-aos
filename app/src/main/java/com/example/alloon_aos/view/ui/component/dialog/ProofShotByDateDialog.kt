package com.example.alloon_aos.view.ui.component.dialog

import ProofShotHomeTransformer
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.alloon_aos.databinding.DialogProofshotByDateBinding
import com.example.alloon_aos.databinding.ItemProofshotCompleteViewpagerBinding
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.RoundedCornersTransformation
import com.example.alloon_aos.viewmodel.PhotiViewModel

class ProofShotByDateDialog(val photiViewModel: PhotiViewModel) : DialogFragment()  {
    private var _binding: DialogProofshotByDateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogProofshotByDateBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)

        binding.viewPager2.adapter = ProofShotByDateAdapter()
        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.setPageTransformer(ProofShotHomeTransformer())

        binding.backImgBtn.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class ViewHolder(private val binding: ItemProofshotCompleteViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setContents(pos: Int) {
            with (photiViewModel.dateProofItems[pos]) {
                binding.bannerTextview.setText(title)
                binding.doneTextview.setText(time)

                if(pos == photiViewModel.dateProofItems.size-1) {
                    binding.dividerGreen.visibility = View.GONE
                }

                binding.root.setOnClickListener {
                    CustomToast.createToast(activity,"피드로 ㄱㄱ")?.show()
                }

                Glide.with(binding.proofshotImageview.context)
                    .load(url)
                    .transform(CenterCrop(), RoundedCornersTransformation(20f, 68f))
                    .into(binding.proofshotImageview)
            }
        }
    }

    inner class ProofShotByDateAdapter() : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProofshotCompleteViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.setContents(position)
        }

        override fun getItemCount() = photiViewModel.dateProofItems.size

    }
}