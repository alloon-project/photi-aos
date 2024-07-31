package com.example.alloon_aos.view.fragment.setting

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.FragmentUnSubscribeBinding
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.example.alloon_aos.view.activity.SettingActivity

class UnSubscribeFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentUnSubscribeBinding
    private lateinit var mContext: Context
    // private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_subscribe, container, false)
        binding.fragment = this
        // binding.viewModel = authViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingActivity
        mActivity.setAppBar(" ")

        setListener()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun setListener(){
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isEmpty()) binding.nextButton.isEnabled = false
                else{
                    binding.nextButton.isEnabled = true
                    password = s.toString()
                }
            }
        })
    }

    fun showInputForm(){
        binding.textView.visibility = View.GONE
        binding.keepButton.visibility = View.GONE
        binding.cancleButton.visibility = View.GONE

        binding.enterPasswordLayout.visibility = View.VISIBLE
        binding.nextButton.visibility = View.VISIBLE
    }

    fun changeInputType(n : Int) {
        val imageButton = binding.hideBtn
        val editText = binding.passwordEditText

        if (editText.inputType == 0x00000091) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_off)
            editText.inputType = 0x00000081
        } else if (editText.inputType == 0x00000081) {
            imageButton.background = mContext.getDrawable(R.drawable.ic_eye_on)
            editText.inputType = 0x00000091
        }
    }

    fun goBack(){
        view?.findNavController()?.navigate(R.id.action_unSubscribeFragment_to_myInfoFragment)
    }

    fun checkPassword(){
        val str = "123"
        if(password.equals(str)){
            goBack()
            CustomToast.createToast(getActivity(),"탈퇴가 완료됐어요. 다음에 또 만나요!")?.show()
        }
        else
            CustomOneButtonDialog(this,"비밀번호가 일치하지 않아요.","다시 입력해 주세요.","알겠어요")
                .show(activity?.supportFragmentManager!!, "CustomDialog")

    }

    override fun onClickYesButton() {
        binding.passwordEditText.setText("")
    }
}