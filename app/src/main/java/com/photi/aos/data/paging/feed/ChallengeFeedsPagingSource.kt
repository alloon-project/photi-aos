package com.photi.aos.data.paging.feed

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.photi.aos.data.model.response.Feed
import com.photi.aos.data.repository.FeedRepository

class ChallengeFeedsPagingSource(private val feedRepository: FeedRepository,private val challengeId : Int, private val sort: String,) : PagingSource<Int, Feed>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        val page = params.key ?: 0
        return try {
            Log.d("ChallengeFeedsPagingSource", "ChallengeFeedsPagingSource Loading page: $page, pageSize: ${params.loadSize}")
            val response = feedRepository.getChallengeFeeds(challengeId, page, params.loadSize,sort)
            val data = response.body()?.data

            if (data == null) {
                Log.e("ChallengeFeedsPagingSource", "API 응답 데이터가 null입니다.")
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

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}
