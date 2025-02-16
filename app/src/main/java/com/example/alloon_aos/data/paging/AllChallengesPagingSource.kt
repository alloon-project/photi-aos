package com.example.alloon_aos.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.response.ChallengeContent
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.PagingListResponse
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.UserRepository
import com.example.alloon_aos.viewmodel.PhotiViewModel

class AllChallengesPagingSource(private val challengeRepository: ChallengeRepository,
    private val photiViewModel: PhotiViewModel) : PagingSource<Int, ChallengeData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeData> {
        val page = params.key ?: 0 // 첫 페이지는 0부터 시작
        return try {
            Log.d("PagingSource", "All Challenges Loading page: $page, pageSize: ${params.loadSize}")

            var content : List<ChallengeData> = emptyList()
            challengeRepository.getChallengeLatest(page, params.loadSize, object :
                ChallengeRepositoryCallback<PagingListResponse> {
                override fun onSuccess(data: PagingListResponse) {
                    val data = data.data
//                    addLatestItem(changeToCommendDataLast(data.content))
//                    latestResponse.value = ActionApiResponse(result)
                    content = data.content
                }

                override fun onFailure(error: Throwable) {
                    photiViewModel.latestResponse.value = ActionApiResponse(ErrorHandler.handle(error))
                }
            })
            //val data = response.body()?.data?.content ?: emptyList() // 데이터 로딩
            LoadResult.Page(
                data = content,
                prevKey = if (page == 0) null else page - 1,  // 첫 페이지는 prevKey가 null
                nextKey = if (content.isEmpty()) null else page + 1  // 다음 페이지가 없으면 null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }



    override fun getRefreshKey(state: PagingState<Int, ChallengeData>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}

