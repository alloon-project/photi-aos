package com.example.alloon_aos.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityFeedBinding
import com.example.alloon_aos.databinding.CustomPopupMenuBinding
import com.example.alloon_aos.view.fragment.feed.IntroduceFragment
import com.example.alloon_aos.view.fragment.feed.PartyMemberFragment
import com.example.alloon_aos.view.fragment.feed.FeedFragment
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialog
import com.example.alloon_aos.view.ui.component.dialog.CustomTwoButtonDialogInterface
import com.example.alloon_aos.view.ui.component.toast.CustomToast
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity(), CustomTwoButtonDialogInterface {
    lateinit var binding : ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed)
        binding.activity = this

        if (savedInstanceState == null) {
            val initialFragment = FeedFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_layout, initialFragment)
                .commit()
        }

        binding.ellipsisImgBtn.setOnClickListener { view ->
            setCustomPopUp(view)
        }

        val tabLayout = binding.tabLayout

        var viewFeedTab = FeedFragment()
        val introduceTab = IntroduceFragment()
        val partyTab = PartyMemberFragment()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                when(tab?.position){
                    0 ->{
                        fragmentTransaction.replace(R.id.frag_layout,viewFeedTab).commit()
                    }
                    1 -> {
                        fragmentTransaction.replace(R.id.frag_layout,introduceTab ).commit()
                    }
                    2 -> {
                        fragmentTransaction.replace(R.id.frag_layout,partyTab).commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //updateTabView(tab, isSelected = false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyBlurEffect(view: View?) {
        val blurEffect = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
        view?.setRenderEffect(blurEffect)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun removeBlurEffect(view: View?) {
        view?.setRenderEffect(null)  // 블러 효과 제거
    }

    private fun updateTabView(tab: TabLayout.Tab?, isSelected: Boolean) {
        val view = tab?.customView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isSelected) {
                removeBlurEffect(view)
            } else {
                applyBlurEffect(view)
            }
        }
    }

    private fun setCustomPopUp(view: View) {
        val popupViewBinding = CustomPopupMenuBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(popupViewBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        with(popupViewBinding){
            optionOne.text = "챌린지 수정하기"
            optionTwo.text = "챌린지 탈퇴하기"

            optionOne.setOnClickListener {
                Toast.makeText(this@FeedActivity, "수정하기 클릭됨", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            optionTwo.setOnClickListener {
                CustomTwoButtonDialog(this@FeedActivity,"챌린지를 탈퇴할까요?","탈퇴해도 기록은 남아있어요.\n" +
                        "탈퇴하시기 전에 직접 지워주세요.","취소할게요","탈퇴할게요")
                    .show(binding.activity?.supportFragmentManager!!,"CustomDialog")
                popupWindow.dismiss()
            }

        }

        popupWindow.showAsDropDown(view, 0, 0, Gravity.CENTER)
    }

    override fun onClickFisrtButton() {}

    override fun onClickSecondButton() {
        //챌린지 탈퇴
        finish()
        val intent = Intent(this, PhotiActivity::class.java).apply {
            putExtra("IS_FROM","UNSUBSCRIBE")
        }
        startActivity(intent)
    }

    fun finishActivity(){
        finish()
    }

}