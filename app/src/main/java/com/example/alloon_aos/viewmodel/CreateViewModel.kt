package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.R

data class Image(val content : String, val image : Int, var select : Boolean)

class CreateViewModel : ViewModel() {
    var selectImage = MutableLiveData<Int>()

    val images = arrayListOf<Image>(
        Image("럭키데이", R.drawable.image222, true),
        Image("인증샷", R.drawable.image33, false),
        Image("오운완", R.drawable.image44, false),
        Image("스터디", R.drawable.image555, false)
    )

    fun initImage() {
        selectImage.value = images[0].image
    }

    fun select(pos : Int) {
        for (n in 0..3) {
            if (n == pos)
                images[n].select = true
            else
                images[n].select = false
        }
        if (pos < 4)
            selectImage.value = images[pos].image
    }
}