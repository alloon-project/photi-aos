package com.example.alloon_aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alloon_aos.data.model.response.EndedChallengeContent
import com.example.alloon_aos.data.repository.UserRepository


class EndedChallengePagingSource(private val userRepository: UserRepository) : PagingSource<Int, EndedChallengeContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EndedChallengeContent> {
        val page = params.key ?: 0
        return try {
            Log.d("PagingSource", "Ended Challenge Loading page: $page, pageSize: ${params.loadSize}")
            val response = userRepository.getEndedChallenges(page, params.loadSize)
            val data = response.body()?.data?.content ?: emptyList()
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EndedChallengeContent>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}