package com.ariadne.backend.storage.adapter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

class GoogleDriveClientTest {

    private val restClientBuilder = RestClient.builder()
    private val mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()
    private val client = GoogleDriveClient(restClientBuilder)

    @Test
    fun `Access Token으로 현재 Google Drive 사용자를 조회한다`() {
        mockServer.expect(requestTo("https://www.googleapis.com/drive/v3/about?fields=user(displayName,emailAddress,permissionId)"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer access-token"))
            .andRespond(
                withSuccess(
                    """
                    {
                      "user": {
                        "displayName": "Taeuk Ha",
                        "emailAddress": "taeuk@example.com",
                        "permissionId": "permission-123"
                      }
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val user = client.getCurrentUser("access-token")

        assertThat(user.displayName).isEqualTo("Taeuk Ha")
        assertThat(user.emailAddress).isEqualTo("taeuk@example.com")
        assertThat(user.permissionId).isEqualTo("permission-123")

        mockServer.verify()
    }
}