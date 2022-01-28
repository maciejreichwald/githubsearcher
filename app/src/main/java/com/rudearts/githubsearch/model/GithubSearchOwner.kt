package com.rudearts.githubsearch.model

import com.google.gson.annotations.SerializedName

data class GithubSearchOwner(
        @SerializedName("login") val login:String?)