package com.example.alloon_aos.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alloon_aos.data.model.response.FeedHistoryContent
import com.example.alloon_aos.data.repository.UserRepository

class FeedHistoryPagingSource(private val userRepository: UserRepository) : PagingSource<Int, FeedHistoryContent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedHistoryContent> {
        val page = params.key ?: 0 // 첫 페이지는 0부터 시작
        return try {
            val response = userRepository.getFeedHistory(page, params.loadSize)
            val data = response.body()?.data?.content ?: emptyList() // 데이터 로딩
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,  // 첫 페이지는 prevKey가 null
                nextKey = if (data.isEmpty()) null else page + 1  // 다음 페이지가 없으면 null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FeedHistoryContent>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}

