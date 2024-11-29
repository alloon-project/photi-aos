package com.example.alloon_aos.view.fragment.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentProfilePasswordBinding
import com.example.alloon_aos.view.activity.AuthActivity
import com.example.alloon_aos.view.activity.SettingsActivity

class ProfilePasswordFragment : Fragment() {
    private lateinit var binding : FragmentProfilePasswordBinding
    private lateinit var mContext: Context
    // private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_password, container, false)
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar(" ")

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getBooleanExtra("IS_FROM_LOGIN",false)
                if(data == true)
                    findNavController().popBackStack()
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun changeInputType(n : Int) {
        var imageButton : ImageButton? = null
        var editText : EditText? = null

        when(n){
            1 -> {
                imageButton = binding.hideBtn1
                editText = binding.passwordEditText
            }
            2 -> {
                imageButton = binding.hideBtn2
                editText = binding.newPasswordEditText
            }
            3 -> {
                imageButton = binding.hideBtn3
                editText = binding.newPasswordEditText2
            }
        }

        if (editText?.inputType == 0x00000091) {
            imageButton?.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText?.inputType = 0x00000081
        } else if (editText?.inputType == 0x00000081) {
            imageButton?.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    fun moveToAuth(){
        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra("IS_FROM_SETTINGS_ACTIVITY",true)
        }
        startForResult.launch(intent)
    }
}