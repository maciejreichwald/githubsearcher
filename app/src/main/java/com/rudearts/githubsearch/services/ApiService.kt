package com.rudearts.githubsearch.services

import com.rudearts.githubsearch.model.GithubSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("Content-Type: application/json")
    @GET("/search/code")
    suspend fun search(@Query("q") query: String):Response<GithubSearchResponse>
}