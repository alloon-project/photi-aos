package com.photi.aos.data.repository

interface MainRepositoryCallback<T> {
    fun onSuccess(data: T)
    fun onFailure(error: Throwable)
}