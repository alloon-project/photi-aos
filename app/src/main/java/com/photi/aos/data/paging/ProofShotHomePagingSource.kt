package com.photi.aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.photi.aos.data.model.response.ChallengeContent
import com.photi.aos.data.repository.UserRepository

class ProofShotHomePagingSource(private val userRepository: UserRepository) : PagingSource<Int, ChallengeContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeContent> {
        val page = params.key ?: 0
        return try {
            Log.d("PagingSource", "ProofShotHome Loading page: $page, pageSize: ${params.loadSize}")
            val response = userRepository.getMyChallenges(page, params.loadSize)
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

    override fun getRefreshKey(state: PagingState<Int, ChallengeContent>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}