package com.example.alloon_aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.viewmodel.SearchViewModel

class SearchNamePagingSource(private val name: String, private val searchViewModel: SearchViewModel, private val challengeRepository: ChallengeRepository) : PagingSource<Int, ChallengeData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeData> {
        val page = params.key ?: 0 // 첫 페이지는 0부터 시작

        return try {
            Log.d("SearchNamePagingSource", "Search History Loading page: $page, pageSize: ${params.loadSize}")

            val response = challengeRepository.getSearchName(name, page, params.loadSize)
            val data = response.body()?.data

            if (data == null) {
                Log.e("SearchNamePagingSource", "API 응답 데이터가 null입니다.")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val content = data.content
            searchViewModel.page.value = content.size
            Log.d("SearchNamePagingSource", "Loaded ${content.size} items")

            LoadResult.Page(
                data = content,
                prevKey = if (data.first) null else page - 1,  // 첫 페이지는 prevKey가 null
                nextKey = if (data.last || data.size == 0) null else page + 1  // 다음 페이지가 없으면 null
            )
        } catch (exception: Exception) {
            Log.e("SearchNamePagingSource", "Error: ${exception.message}")
            LoadResult.Error(exception)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, ChallengeData>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}

