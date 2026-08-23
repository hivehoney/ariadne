package com.ariadne.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ariadne.android.data.remote.AriadneApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackendHealthInstrumentedTest {

    @Test
    fun backendHealthReturnsOk() {
        val response = AriadneApiClient.api.health().execute()

        assertTrue(response.isSuccessful)
        assertEquals("OK", response.body())
    }
}