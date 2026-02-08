package com.photi.aos.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.MyData
import com.photi.aos.data.model.request.CreateData
import com.photi.aos.data.model.request.HashTag
import com.photi.aos.data.model.request.MemberImg
import com.photi.aos.data.model.request.ModifyData
import com.photi.aos.data.model.request.Rule
import com.photi.aos.data.model.response.ChallengeResponse
import com.photi.aos.data.model.response.MatchResponse
import com.photi.aos.data.model.response.MessageResponse
import com.photi.aos.data.remote.PresignedPutUploader
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ChallengeRepository
import com.photi.aos.data.repository.ChallengeRepositoryCallback
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.SettingsRepository
import com.photi.aos.data.storage.MyChallengeList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ChallengeViewModel : ViewModel() {
    companion object {
        private const val TAG = "CHALLENGE"
    }

    private val challengeService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(challengeService)

    private val authService = RetrofitClient.authService
    private val settingsRepository = SettingsRepository(authService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val joinResponse = MutableLiveData<ActionApiResponse>()

    var id = -1
    var name = ""
    var isPublic = true
    var goal = ""
    var proveTime = ""
    var endDate = ""
    var rules: List<Rule> = listOf()
    var hashs: List<HashTag> = listOf()
    var imageFile = ""
    var currentMemberCnt = 0
    var memberImages: List<MemberImg> = listOf()

    var isUri = false
    var _img = MutableLiveData<String>()
    var invitecode = ""

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
        joinResponse.value = ActionApiResponse()
    }

    fun checkUserInChallenge(): Boolean {
        return MyChallengeList.checkUserInChallenge(id)
    }

    fun setChallengeId(id: Int) {
        this.id = id
    }

    fun setChallengeData(data: MyData) {
        name = data.name
        isPublic = data.isPublic
        goal = data.goal
        proveTime = data.proveTime
        endDate = data.endDate
        setRuleData(data.rules)
        setHashData(data.hashtags)
        setImgData(data.imageUrl.toString())
        currentMemberCnt = data.currentMemberCnt
        memberImages = data.memberImages
    }

    fun setTitleData(title: String) { name = title }
    fun setTimeData(time: String) { proveTime = time }
    fun setGoalData(goal: String) { this.goal = goal }
    fun setDateDate(date: String) { endDate = date }
    fun setRuleData(rules: List<Rule>) { this.rules = rules }
    fun setHashData(hashs: List<HashTag>) { this.hashs = hashs }
    fun setImgData(img: String) {
        imageFile = img
        _img.value = img
    }
    fun setIsUri(boolean: Boolean) {
        isUri = boolean
    }


    fun makeFile(context : Context, callback: (Pair<File, String>?) -> Unit){
        try {
            if (isUri) {
                val (file, mime) = getFileFromUri(Uri.parse(imageFile), context)!!
                callback(file to mime)
            } else {
                downloadImage(imageFile, context) { pair ->
                    if (pair != null) {
                        callback(pair.first to pair.second)
                    }
                    else {
                        callback(null)
                        Log.e("ImageDownload", "Failed to download image")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    fun getFileFromUri(uri: Uri, context: Context): Pair<File, String>? {
        val fileName = uri.lastPathSegment // 또는 적절한 이름을 직접 지정할 수 있음
        val file = File(context.cacheDir, fileName)

        try {
            val mime = context.contentResolver.getType(uri)!!

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file to mime
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun downloadImage(imageUrl: String, context: Context, callback: (Pair<File, String>?) -> Unit) {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val mime = connection.contentType

                val fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)
                val file = File(context.cacheDir, fileName)

                val inputStream: InputStream = connection.inputStream
                val outputStream = FileOutputStream(file)

                inputStream.copyTo(outputStream)

                callback(file to mime)

                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                callback(null)
            }
        }.start()
    }

    fun makePresignedUrl(file: File, mime: String, action: String) {
        viewModelScope.launch {
            try{
                val presignedUrl = PresignedPutUploader.getPresignedUrl(
                    call = { settingsRepository.postUserImagePresignedUrl(file.name) },
                    extractor = { body -> body.data.preSignedUrl }
                )
                withContext(Dispatchers.IO) {
                    PresignedPutUploader.putFile(presignedUrl, file, mime)
                }

                when (action) {
                    "create" -> { createChallenge(presignedUrl) }
                    "modify" -> { modifyChallenge(presignedUrl) }
                    else -> { Log.d(TAG, "Unknown Action") }
                }
            }catch (e : Exception){
                Log.e(TAG, "Failed to make Presigned Url")
            }finally {

            }
        }
    }

    fun modifyChallenge(presignedUrl: String) {
        repository.modifyChallenge(id, ModifyData(name, goal, proveTime, endDate, presignedUrl, rules, hashs), object :
            ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ActionApiResponse(result, "modifyChallenge")
                Log.d(TAG, "modifyChallenge: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun createChallenge(presignedUrl: String) {
        repository.createChallenge(CreateData(name, isPublic, goal, proveTime, endDate, presignedUrl, rules, hashs), object :
            ChallengeRepositoryCallback<ChallengeResponse> {
            override fun onSuccess(data: ChallengeResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                setChallengeId(data.id)
                apiResponse.value = ActionApiResponse(result, "createChallenge")
                Log.d(TAG, "createChallenge: $id $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun matchInviteCode() {
        repository.getChallengeCodeMatch(id, invitecode, object  : ChallengeRepositoryCallback<MatchResponse> {
            override fun onSuccess(data: MatchResponse) {
                val result = data.code
                val mes = data.message
                val isMatch = data.data.isMatch
                joinResponse.value = ActionApiResponse(result, isMatch.toString())
                Log.d(TAG, "matchInviteCode: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                val errorCode = ErrorHandler.handle(error)
                joinResponse.value = ActionApiResponse(errorCode)
            }
        })
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        apiResponse.value = ActionApiResponse(errorCode)
    }

}