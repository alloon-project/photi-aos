package com.example.alloon_aos.data.paging.feed

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alloon_aos.data.model.response.Comment
import com.example.alloon_aos.data.repository.FeedRepository

class FeedCommentsPagingSource(
    private val feedRepository: FeedRepository,
    private val feedId: Int
) : PagingSource<Int, Comment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 0
        return try {
            Log.d("FeedCommentsPagingSource", "Loading page: $page, pageSize: ${params.loadSize}")
            val response = feedRepository.getFeedComments(feedId, page, params.loadSize)
            val data = response.body()?.data

            if (data == null) {
                Log.e("FeedCommentsPagingSource", "API 응답 데이터가 null입니다.")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            LoadResult.Page(
                data = data.content,
                prevKey = if (data.first) null else page - 1,
                nextKey = if (data.last || data.size == 0) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("FeedCommentsPagingSource", "Error: ${e.localizedMessage}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}
