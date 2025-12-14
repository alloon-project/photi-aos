package com.photi.aos.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException

object PresignedPutUploader {
    private val client = OkHttpClient() // 업로드 전용(인증 인터셉터 X)

    /**
     * 어떤 endpoint든 "Response<T>"만 주면 presignedUrl을 String으로 뽑아줌
     */
    suspend fun <T> getPresignedUrl(
        call: suspend () -> Response<T>,
        extractor: (T) -> String
    ): String {
        val response = call()
        if (!response.isSuccessful) throw IOException("Presigned API failed: ${response.code()}")
        val body = response.body() ?: throw IOException("Empty body")
        return extractor(body)
    }

    suspend fun putFile(url: String, file: File, contentType: String) = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url(url)
            .put(file.asRequestBody(contentType.toMediaType()))
            .header("Content-Type", contentType)
            .build()

        client.newCall(req).execute().use { res ->
            if (!res.isSuccessful) throw IOException("Upload failed: ${res.code}")
        }
    }
}
