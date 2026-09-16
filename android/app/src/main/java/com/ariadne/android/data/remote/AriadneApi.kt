package com.ariadne.android.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface AriadneApi {
    @GET("health")
    fun health(): Call<String>
}