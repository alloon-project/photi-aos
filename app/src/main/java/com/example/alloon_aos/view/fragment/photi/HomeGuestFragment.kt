package com.example.alloon_aos.view.fragment.photi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.R
import com.example.alloon_aos.data.repository.TokenManager
import com.example.alloon_aos.databinding.FragmentHomeGuestBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.PhotiActivity
import com.example.alloon_aos.view.adapter.GuestHomeAdapter
import com.example.alloon_aos.viewmodel.PhotiViewModel

class HomeGuestFragment : Fragment() {
    private lateinit var binding : FragmentHomeGuestBinding
    private val photiViewModel by activityViewModels<PhotiViewModel>()
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_guest, container, false)
        binding.fragment = this
        binding.viewModel = photiViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.guestRecyclerview.adapter = GuestHomeAdapter(photiViewModel)
        binding.guestRecyclerview.layoutManager = GridLayoutManager(context, 3)
        binding.guestRecyclerview.setHasFixedSize(true)

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        binding.nextBtn.setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

}