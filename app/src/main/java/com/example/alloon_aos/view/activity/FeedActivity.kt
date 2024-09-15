package com.example.alloon_aos.view.activity

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alloon_aos.R
import com.example.alloon_aos.databinding.ActivityFeedBinding
import com.example.alloon_aos.view.fragment.feed.IntroduceFragment
import com.example.alloon_aos.view.fragment.feed.PartyMemberFragment
import com.example.alloon_aos.view.fragment.feed.FeedFragment
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity() {
    lateinit var binding : ActivityFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed)
        binding.activity = this

        if (savedInstanceState == null) {
            // 처음에 표시할 프래그먼트
            val initialFragment = FeedFragment()

            // FragmentManager를 통해 프래그먼트 트랜잭션 시작
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_layout, initialFragment)  // 프래그먼트 컨테이너 뷰의 ID와 프래그먼트 인스턴스
                .commit()
        }

        val tabLayout = binding.tabLayout

        val tabTitles = listOf("피드", "소개", "파티원")

//        tabTitles.forEach { title ->
//            val tab = tabLayout.newTab()
//            tab.customView = createTabView(title)
//            tabLayout.addTab(tab)
//        }

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


//    private fun createTabView(title: String): View {
//        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
//        val textView = view.findViewById<TextView>(R.id.tab_title)
//        textView.text = title
//        return view
//    }

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

    fun finishActivity(){
        finish()
    }
}