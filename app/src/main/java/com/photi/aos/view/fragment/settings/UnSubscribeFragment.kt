package com.photi.aos.view.fragment.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.photi.aos.R
import com.photi.aos.databinding.FragmentUnSubscribeBinding
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialog
import com.photi.aos.view.ui.component.dialog.CustomOneButtonDialogInterface
import com.photi.aos.view.ui.component.toast.CustomToast
import com.photi.aos.view.activity.SettingsActivity
import com.photi.aos.viewmodel.SettingsViewModel

class UnSubscribeFragment : Fragment(), CustomOneButtonDialogInterface {
    private lateinit var binding : FragmentUnSubscribeBinding
    private lateinit var mContext: Context
    private val settingsViewmodel by activityViewModels<SettingsViewModel>()
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_subscribe, container, false)
        binding.fragment = this
        binding.viewModel = settingsViewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        val mActivity = activity as SettingsActivity
        mActivity.setAppBar(" ")

        setListener()
        setObserve()
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
        view?.findNavController()?.popBackStack()
    }


    fun setObserve() {
        settingsViewmodel.actionApiResponse.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                "200 OK" -> {
                    val mActivity = activity as SettingsActivity
                    mActivity.unsubscribe()
                }

                "LOGIN_UNAUTHENTICATED" -> {
                    CustomOneButtonDialog(this,"비밀번호가 일치하지 않아요.","다시 입력해 주세요.","알겠어요")
                        .show(activity?.supportFragmentManager!!, "CustomDialog")
                }

                "IO_Exception" -> {
                    CustomToast.createToast(activity, "네트워크가 불안정해요. 다시 시도해주세요.", "circle")?.show()
                }

                "TOKEN_UNAUTHORIZED" -> {
                    Log.d("Observer", "response code: ${response.code} ")
                }


                else -> {
                    Log.d("Observer", "Unhandled response code: ${response.code}")
                }
            }
        }
    }

    override fun onClickYesButton() {
        binding.passwordEditText.setText("")
    }
}