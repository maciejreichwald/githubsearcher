package com.rudearts.githubsearch.services

sealed class NetworkResult <out T>

object Loading: NetworkResult<Nothing>()

data class NetworkSuccess<out T>(val data:T): NetworkResult<T>()
data class NetworkError(val exception:Throwable): NetworkResult<Nothing>()