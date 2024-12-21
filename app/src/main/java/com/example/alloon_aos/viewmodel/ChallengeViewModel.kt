package com.example.alloon_aos.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.request.CreateData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.ModifyData
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
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

    val apiResponse = MutableLiveData<ActionApiResponse>()

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

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
    }

    fun setChallengeId(id: Int) {
        this.id = id
    }

    fun setChallengeData(data: MyData) {
        name = data.name
        isPublic = data.isPublic
        goal = data.goal
        proveTime = data.proveTime
        endDate = data.endData
        setRuleData(data.rules)
        setHashData(data.hashtags)
        setImgData(data.imageUrl.toString())
        currentMemberCnt = data.currentMemberCnt
        memberImages = data.memberImages
    }

    fun getData() : MyData { //테스트용
        var data = MyData(name, isPublic, goal, proveTime, endDate, rules, hashs)
        return data
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


    fun makeFile(context : Context, callback: (File?) -> Unit){
        try {
            if (isUri) {
                val makefile = getFileFromUri(Uri.parse(imageFile), context)
                callback(makefile)
            } else {
                downloadImage(imageFile, context) { file ->
                    if (file != null) {
                        callback(file)
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

    fun getFileFromUri(uri: Uri, context: Context): File? {
        val fileName = uri.lastPathSegment // 또는 적절한 이름을 직접 지정할 수 있음
        val file = File(context.cacheDir, fileName)

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun downloadImage(imageUrl: String, context: Context, callback: (File?) -> Unit) {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val inputStream: InputStream = connection.inputStream

                val fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)
                val file = File(context.cacheDir, fileName)

                val outputStream = FileOutputStream(file)

                inputStream.copyTo(outputStream)

                callback(file)

                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                callback(null)
            }
        }.start()
    }


    fun modifyChallenge(context: Context) {
        makeFile(context) { file ->
            if (file != null) {
                repository.modifyChallenge(id, file, ModifyData(name, goal, proveTime, endDate, rules, hashs), object :
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
            } else {
                Log.e(TAG, "Failed to make file")
            }
        }
    }


    fun createChallenge(context : Context) {
        makeFile(context) { file ->
            if (file != null) {
                repository.createChallenge(file, CreateData(name, isPublic, goal, proveTime, endDate, rules, hashs), object :
                    ChallengeRepositoryCallback<ChallengeResponse> {
                    override fun onSuccess(data: ChallengeResponse) {
                        val result = data.code
                        val mes = data.message
                        val data = data.data
                        id = data.id
                        apiResponse.value = ActionApiResponse(result, "createChallenge")
                        Log.d(TAG, "createChallenge: $id $mes $result")
                    }

                    override fun onFailure(error: Throwable) {
                        handleFailure(error)
                    }
                })
            } else {
                Log.e(TAG, "Failed to make file")
            }
        }
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        apiResponse.value = ActionApiResponse(errorCode)
    }

}