package com.ariadne.backend.storage.adapter

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

class GoogleDriveOAuthClientTest {

    private val restClientBuilder = RestClient.builder()
    private val mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()

    private val client = GoogleDriveOAuthClient(
        restClientBuilder = restClientBuilder,
        clientId = "test-client-id",
        clientSecret = "test-client-secret",
        redirectUri = "",
    )

    @Test
    fun `Authorization Code를 Google Token으로 교환한다`() {
        // given
        val expectedFormData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", "test-client-id")
            add("client_secret", "test-client-secret")
            add("code", "authorization-code")
            add("grant_type", "authorization_code")
            add("redirect_uri", "")
        }

        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().formData(expectedFormData))
            .andRespond(
                withSuccess(
                    """
                    {
                      "access_token": "access-token",
                      "expires_in": 3600,
                      "refresh_token": "refresh-token",
                      "scope": "https://www.googleapis.com/auth/drive.metadata.readonly",
                      "token_type": "Bearer"
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when
        val token = client.exchangeAuthorizationCode("authorization-code")

        // then
        assertThat(token.accessToken).isEqualTo("access-token")
        assertThat(token.refreshToken).isEqualTo("refresh-token")
        assertThat(token.expiresIn).isEqualTo(3600)
        assertThat(token.tokenType).isEqualTo("Bearer")

        mockServer.verify()
    }

    @Test
    fun `Refresh Token이 없는 응답도 처리한다`() {
        // given
        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andRespond(
                withSuccess(
                    """
                    {
                      "access_token": "access-token",
                      "expires_in": 3600,
                      "scope": "https://www.googleapis.com/auth/drive.metadata.readonly",
                      "token_type": "Bearer"
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when
        val token = client.exchangeAuthorizationCode("authorization-code")

        // then
        assertThat(token.refreshToken).isNull()

        mockServer.verify()
    }

    @Test
    fun `Google Token 교환에 실패하면 OAuth 예외가 발생한다`() {
        // given
        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andRespond(withBadRequest())

        // when & then
        assertThatThrownBy {
            client.exchangeAuthorizationCode("invalid-code")
        }.isInstanceOf(GoogleDriveOAuthException::class.java)
            .hasMessageContaining("HTTP 400")

        mockServer.verify()
    }

    @Test
    fun `Authorization Code는 비어 있을 수 없다`() {
        assertThatThrownBy {
            client.exchangeAuthorizationCode(" ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `Refresh Token으로 새로운 Access Token을 발급한다`() {
        // given
        val expectedFormData = LinkedMultiValueMap<String, String>().apply {
            add("client_id", "test-client-id")
            add("client_secret", "test-client-secret")
            add("refresh_token", "refresh-token")
            add("grant_type", "refresh_token")
        }

        mockServer.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().formData(expectedFormData))
            .andRespond(
                withSuccess(
                    """
                {
                  "access_token": "new-access-token",
                  "expires_in": 3600,
                  "scope": "https://www.googleapis.com/auth/drive.metadata.readonly",
                  "token_type": "Bearer"
                }
                """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when
        val token = client.refreshAccessToken("refresh-token")

        // then
        assertThat(token.accessToken).isEqualTo("new-access-token")
        assertThat(token.expiresIn).isEqualTo(3600)
        assertThat(token.tokenType).isEqualTo("Bearer")

        mockServer.verify()
    }

    @Test
    fun `Refresh Token은 비어 있을 수 없다`() {
        assertThatThrownBy {
            client.refreshAccessToken(" ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}