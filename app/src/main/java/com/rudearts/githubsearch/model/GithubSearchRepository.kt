package com.rudearts.githubsearch.model

import com.google.gson.annotations.SerializedName

data class GithubSearchRepository(
    @SerializedName("full_name") val name:String?,
    @SerializedName("owner") val owner:GithubSearchOwner?,
    @SerializedName("description") val description:String?)