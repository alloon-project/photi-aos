package com.example.alloon_aos.view.fragment.create

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentCreateHashtagBinding
import com.example.alloon_aos.view.activity.ChallengeActivity
import com.example.alloon_aos.view.activity.CreateActivity
import com.example.alloon_aos.view.adapter.AddHashAdapter
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.ui.util.KeyboardListener
import com.example.alloon_aos.view.ui.util.OnKeyboardVisibilityListener
import com.example.alloon_aos.viewmodel.CreateViewModel

class CreateHashtagFragment : Fragment() {
    private lateinit var binding : FragmentCreateHashtagBinding
    private lateinit var mContext: Context
    private lateinit var addHashAdapter: AddHashAdapter
    private val createViewModel by activityViewModels<CreateViewModel>()
    private lateinit var mActivity: CreateActivity
    private var hash = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_hashtag, container, false)
        binding.fragment = this
        binding.viewModel = createViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mActivity = activity as CreateActivity
        mActivity.setAppBar()

        if (mActivity.isFromChallenge)
            setModifyLayout()

        addHashAdapter = AddHashAdapter(createViewModel)
        binding.hashRecyclerview.adapter = addHashAdapter
        binding.hashRecyclerview.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.hashRecyclerview.setHasFixedSize(true)

        setListener()
        setObserve()

        ObjectAnimator.ofInt(binding.progress, "progress", 80,100)
            .setDuration(500)
            .start()

        return binding.root
    }

    private fun setModifyLayout() {
        mActivity.setTitle("챌린지 해시태그 수정")
        binding.progress.visibility = View.GONE
        binding.nextBtn.setText("저장하기")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setListener() {
        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        KeyboardListener.setKeyboardVisibilityListener(binding.root,object :
            OnKeyboardVisibilityListener {
            override fun onVisibilityChanged(visible: Boolean) {
                if (visible)    binding.hashEdittext.background = mContext.getDrawable(R.drawable.input_line_focus)
                else    binding.hashEdittext.background = mContext.getDrawable(R.drawable.input_line_default)
            }
        })

        binding.hashEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s!!.length
                if (length > 6 || length == 0) {
                    binding.addHashBtn.isEnabled = false
                    binding.checkLengthTextView.setTextColor(mContext.getColor(R.color.gray400))
                    binding.checkLengthIconView.setColorFilter(mContext.getColor(R.color.gray400))
                } else {
                    binding.addHashBtn.isEnabled = true
                    binding.checkLengthTextView.setTextColor(mContext.getColor(R.color.blue400))
                    binding.checkLengthIconView.setColorFilter(mContext.getColor(R.color.blue400))
                }
            }
        })

        binding.addHashBtn.setOnClickListener {
            hash = binding.hashEdittext.text.toString()
            if (createViewModel._hashs.size == 3)
                CustomToast.createToast(activity,"해시태그는 3개까지 등록 가능해요")?.show()
            else
                createViewModel.addHash(hash)
        }

        binding.nextBtn.setOnClickListener {
            if (mActivity.isFromChallenge)
                requireActivity().finish()
            else {
                val intent = Intent(requireContext(), ChallengeActivity::class.java)
                intent.putExtra("IS_FROM_CREATE",true)
                startActivity(intent)
            }
        }
    }

    private fun setObserve() {
        createViewModel.hashs.observe(viewLifecycleOwner) {
            addHashAdapter.notifyDataSetChanged()
            binding.nextBtn.isEnabled = it.size != 0
        }
    }

}