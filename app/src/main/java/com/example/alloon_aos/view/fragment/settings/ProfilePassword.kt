package com.example.alloon_aos.view.fragment.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentProfileModifyBinding
import com.example.alloon_aos.databinding.FragmentProfilePasswordBinding
import com.example.alloon_aos.view.activity.AuthActivity

class ProfilePassword : Fragment() {
    private lateinit var binding : FragmentProfilePasswordBinding
    private lateinit var mContext: Context
    // private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_password, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun changeInputType(n : Int) {
        val imageButton = if (n == 1) binding.hide1Btn else binding.hide2Btn
        val editText = if (n == 1) binding.newPassword1EditText else binding.newPassword2EditText

        if (editText.inputType == 0x00000091) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    fun moveToAuth(){
        val intent = Intent(activity, AuthActivity::class.java).apply {
            putExtra("IS_FROM_SETTINGS_ACTIVITY",true)
        }
        startActivity(intent)
        //view?.findNavController()?.navigate(R.id.action_profilePasswordFragment_to_passwordSendFragment)
    }
}