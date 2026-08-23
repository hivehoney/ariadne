package com.ariadne.android.data

import com.ariadne.android.data.remote.AriadneApiClient

class BackendRepository {

    fun checkHealth(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        AriadneApiClient.api.health().enqueue(
            object : retrofit2.Callback<String> {
                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                    if (response.isSuccessful && response.body() == "OK") {
                        onSuccess()
                    } else {
                        onFailure(
                            IllegalStateException("Backend response: ${response.code()}")
                        )
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<String>,
                    t: Throwable
                ) {
                    onFailure(t)
                }
            }
        )
    }
}