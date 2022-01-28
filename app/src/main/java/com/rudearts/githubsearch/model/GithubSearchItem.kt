package com.rudearts.githubsearch.model

import com.google.gson.annotations.SerializedName

data class GithubSearchItem(
    @SerializedName("repository") val repo:GithubSearchRepository?,
    @SerializedName("html_url") val url:String?)