package com.photi.aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photi.aos.data.enum.CategoryType
import com.photi.aos.data.enum.ReasonType
import com.photi.aos.data.model.request.ReportRequest
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.FeedRepository
import com.photi.aos.data.repository.handleApiCall
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    companion object {
        private const val TAG = "REPORT"
    }

    private val feedService = RetrofitClient.feedService
    private val feedRepository = FeedRepository(feedService)

    private val _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code

    lateinit var category : CategoryType
    lateinit var reason : ReasonType
    var targetId = -1
    var content = ""


    fun sendReport() {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.postReport(targetId, ReportRequest(category, reason, content)) },
                onSuccess = { data ->
                    _code.value = "201 CREATED"
                },
                onFailure = { errorCode ->
                    _code.value = errorCode
                }
            )
        }
    }
}