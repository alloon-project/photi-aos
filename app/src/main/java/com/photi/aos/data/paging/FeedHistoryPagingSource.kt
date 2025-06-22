package com.photi.aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.photi.aos.data.model.response.FeedHistoryContent
import com.photi.aos.data.repository.UserRepository

class FeedHistoryPagingSource(private val userRepository: UserRepository) : PagingSource<Int, FeedHistoryContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedHistoryContent> {
        val page = params.key ?: 0 // 첫 페이지는 0부터 시작

        return try {
            Log.d("FeedHistoryPagingSource", "Feed History Loading page: $page, pageSize: ${params.loadSize}")

            val response = userRepository.getFeedHistory(page, params.loadSize)
            val data = response.body()?.data
            if (data == null) {
                Log.e("FeedHistoryPagingSource", "API 응답 데이터가 null입니다.")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val content = data.content
            Log.d("FeedHistoryPagingSource", "Loaded ${content.size} items")

            LoadResult.Page(
                data = content,
                prevKey = if (data.first) null else page - 1,
                nextKey = if (data.last || data.size == 0) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FeedHistoryContent>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}

