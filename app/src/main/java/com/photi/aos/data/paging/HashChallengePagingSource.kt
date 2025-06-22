package com.photi.aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.photi.aos.data.model.response.ChallengeData
import com.photi.aos.data.repository.ChallengeRepository

class HashChallengePagingSource(private val hashtag: String, private val challengeRepository: ChallengeRepository) : PagingSource<Int, ChallengeData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeData> {
        val page = params.key ?: 0 // 첫 페이지는 0부터 시작

        return try {
            Log.d("HashChallengePagingSource", "Hash Challenge page: $page, pageSize: ${params.loadSize}")

            val response = challengeRepository.getChallengeHashtag(hashtag, page, params.loadSize)
            val data = response.body()?.data

            if (data == null) {
                Log.e("HashChallengePagingSource", "API 응답 데이터가 null입니다.")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val content = data.content
            Log.d("HashChallengePagingSource", "Loaded ${content.size} items")

            LoadResult.Page(
                data = content,
                prevKey = if (data.first) null else page - 1,  // 첫 페이지는 prevKey가 null
                nextKey = if (data.last || data.size == 0) null else page + 1  // 다음 페이지가 없으면 null
            )
        } catch (exception: Exception) {
            Log.e("HashChallengePagingSource", "Error: ${exception.message}")
            LoadResult.Error(exception)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, ChallengeData>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}

