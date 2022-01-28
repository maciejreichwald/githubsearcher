package com.rudearts.githubsearch.services

import android.net.Uri
import com.rudearts.githubsearch.model.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkService {

    companion object {
        const val REPO_QUERY = "repo:"
        const val ORG_QUERY = "org:"
        const val USER_QUERY = "user:"

        const val DEFAULT_QUERY = "kotlin"
    }

    private val apiService by lazy { RetrofitBuilder.apiService }

    fun loadMessages(query:String?, filterType:Int, filterText:String): Flow<NetworkResult<List<GithubRepository>>> = flow {
        emit(Loading)
        try {
            val parsedQuery = parseQuery(query)
            val parseFilterType = parseFilterType(filterType)

            val response = apiService.search("$parsedQuery+$parseFilterType$filterText")

            val news:List<GithubRepository> = response.body()?.items?.map { item ->
                GithubRepository(
                    item.repo?.owner?.login ?: "",
                    item.repo?.name ?: "",
                    item.repo?.description ?: "",
                    item.url ?: "")
            }
                ?: emptyList()
            emit(NetworkSuccess(news))
        } catch (throwable: Throwable) {
            emit(NetworkError(throwable))
        }
    }

    private fun parseFilterType(filterType: Int): String = when(filterType) {
        0 -> ORG_QUERY
        1 -> REPO_QUERY
        else -> USER_QUERY
    }

    private fun parseQuery(query: String?):String = when {
        query?.isNotEmpty() == true -> Uri.encode(query)
        else -> DEFAULT_QUERY
    }
}