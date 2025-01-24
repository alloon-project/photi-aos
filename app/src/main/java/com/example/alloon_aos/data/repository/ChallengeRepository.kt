package com.example.alloon_aos.data.repository

import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.data.model.request.CreateData
import com.example.alloon_aos.data.model.request.Goal
import com.example.alloon_aos.data.model.request.ModifyData
import com.example.alloon_aos.data.model.response.ChallengeListResponse
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.CodeResponse
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.ExamImgResponse
import com.example.alloon_aos.data.model.response.PagingListResponse
import com.example.alloon_aos.data.remote.ChallengeService
import com.example.alloon_aos.data.storage.TokenManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


interface ChallengeRepositoryCallback<T> {
    fun onSuccess(data: T)
    fun onFailure(error: Throwable)
}

class ChallengeRepository(private val challengeService: ChallengeService) {
    private val tokenManager = TokenManager(MyApplication.mySharedPreferences)

    fun createImagePart(file: File): MultipartBody.Part {
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
    }

    //DELETE
    fun deleteChallenge(id:Int, callback: ChallengeRepositoryCallback<MessageResponse>) {
        challengeService.delete_exitChallenge(id).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }


    //GET
    fun getChallengeLatest(page: Int, size: Int, callback: ChallengeRepositoryCallback<PagingListResponse>) {
        challengeService.get_challengeLatest(page = page, size = size).enqueue(object : Callback<PagingListResponse> {
            override fun onResponse(call: Call<PagingListResponse>, response: Response<PagingListResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<PagingListResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun getChallengeCode(id: Int, callback: ChallengeRepositoryCallback<CodeResponse>) {
        challengeService.get_challengeCode(id).enqueue(object : Callback<CodeResponse> {
            override fun onResponse(call: Call<CodeResponse>, response: Response<CodeResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<CodeResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun getChallengeInfo(id: Int, callback: ChallengeRepositoryCallback<ChallengeResponse>) {
        challengeService.get_challengeInfo(id).enqueue(object : Callback<ChallengeResponse> {
            override fun onResponse(call: Call<ChallengeResponse>, response: Response<ChallengeResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<ChallengeResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    // 챌린지 이름 검색

    // 챌린지 해시태그 검색

    fun getChallengePopular(callback: ChallengeRepositoryCallback<ChallengeListResponse>) {
        challengeService.get_challengePopular().enqueue(object : Callback<ChallengeListResponse> {
            override fun onResponse(call: Call<ChallengeListResponse>, response: Response<ChallengeListResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<ChallengeListResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    //해시태그리스트조회

    fun getExamImg(callback: ChallengeRepositoryCallback<ExamImgResponse>) {
        challengeService.get_exampleImg().enqueue(object : Callback<ExamImgResponse> {
            override fun onResponse(call: Call<ExamImgResponse>, response: Response<ExamImgResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<ExamImgResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun getChallengeHashtag(hashtag: String, page: Int, size: Int, callback: ChallengeRepositoryCallback<PagingListResponse>) {
        challengeService.get_challengeHashtag(hashtag = hashtag, page = page, size = size).enqueue(object : Callback<PagingListResponse> {
            override fun onResponse(call: Call<PagingListResponse>, response: Response<PagingListResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<PagingListResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }


    //PATCH
    fun modifyChallenge(challengeId: Int, imageFile: File, challenge: ModifyData, callback: ChallengeRepositoryCallback<MessageResponse>) {
        val gson = Gson()
        val json = gson.toJson(challenge)
        val dataPart = json.toRequestBody("application/json".toMediaTypeOrNull())
        val imagePart = createImagePart(imageFile)

        challengeService.patch_modifyChallenge(challengeId, dataPart, imagePart).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun setGoal(id: Int, goal: Goal, callback: ChallengeRepositoryCallback<MessageResponse>) {
        challengeService.patch_setGoal(id, goal).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }


    //POST
    fun createChallenge(imageFile: File, challenge: CreateData, callback: ChallengeRepositoryCallback<ChallengeResponse>) {
        val gson = Gson()
        val json = gson.toJson(challenge)
        val dataPart = json.toRequestBody("application/json".toMediaTypeOrNull())
        val imagePart = createImagePart(imageFile)

        challengeService.post_createChallenge(dataPart, imagePart).enqueue(object : Callback<ChallengeResponse> {
            override fun onResponse(call: Call<ChallengeResponse>, response: Response<ChallengeResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<ChallengeResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    //공개 챌린지 참여

    //비공개 챌린지 참여
}