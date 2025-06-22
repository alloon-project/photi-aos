package com.photi.aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.photi.aos.data.model.response.EndedChallengeContent
import com.photi.aos.data.repository.UserRepository


class EndedChallengePagingSource(private val userRepository: UserRepository) : PagingSource<Int, EndedChallengeContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EndedChallengeContent> {
        val page = params.key ?: 0
        return try {
            Log.d("EndedChallengePagingSource", "Ended Challenge Loading page: $page, pageSize: ${params.loadSize}")
            val response = userRepository.getEndedChallenges(page, params.loadSize)
            val data = response.body()?.data
            if (data == null) {
                Log.e("EndedChallengePagingSource", "API 응답 데이터가 null입니다.")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val content = data.content
            LoadResult.Page(
                data = content,
                prevKey = if (data.first) null else page - 1,
                nextKey = if (data.last || data.size == 0) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EndedChallengeContent>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}