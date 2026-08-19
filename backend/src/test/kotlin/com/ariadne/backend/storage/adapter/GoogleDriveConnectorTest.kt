package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.domain.StorageCredentialType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

class GoogleDriveConnectorTest {

    private val oauthBuilder = RestClient.builder()
    private val oauthServer = MockRestServiceServer.bindTo(oauthBuilder).build()

    private val driveBuilder = RestClient.builder()
    private val driveServer = MockRestServiceServer.bindTo(driveBuilder).build()

    private val oauthClient = GoogleDriveOAuthClient(
        restClientBuilder = oauthBuilder,
        clientId = "test-client-id",
        clientSecret = "test-client-secret",
        redirectUri = "",
    )

    private val driveClient = GoogleDriveClient(driveBuilder)

    private val connector = GoogleDriveConnector(
        googleDriveOAuthClient = oauthClient,
        googleDriveClient = driveClient,
    )

    @Test
    fun `Authorization Code로 Google Drive 연결 결과를 생성한다`() {
        // given
        oauthServer.expect(requestTo("https://oauth2.googleapis.com/token"))
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

        driveServer.expect(
            requestTo(
                "https://www.googleapis.com/drive/v3/about" +
                        "?fields=user(displayName,emailAddress,permissionId)",
            ),
        )
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

        // when
        val result = connector.connect(
            GoogleDriveConnectionRequest("authorization-code"),
        )

        // then
        assertThat(result.displayName).isEqualTo("taeuk@example.com")
        assertThat(result.credentialType).isEqualTo(StorageCredentialType.OAUTH2)
        assertThat(result.externalAccountId).isEqualTo("permission-123")
        assertThat(result.scope)
            .isEqualTo("https://www.googleapis.com/auth/drive.metadata.readonly")
        assertThat(result.credentialSchemaVersion)
            .isEqualTo(GoogleDriveCredentialData.SCHEMA_VERSION)

        val credentialData = result.credentialData as GoogleDriveCredentialData
        assertThat(credentialData.refreshToken).isEqualTo("refresh-token")

        oauthServer.verify()
        driveServer.verify()
    }

    @Test
    fun `Refresh Token이 발급되지 않으면 Google Drive 연결에 실패한다`() {
        // given
        oauthServer.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andRespond(
                withSuccess(
                    """
                    {
                      "access_token": "access-token",
                      "expires_in": 3600,
                      "token_type": "Bearer"
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when & then
        assertThatThrownBy {
            connector.connect(
                GoogleDriveConnectionRequest("authorization-code"),
            )
        }.isInstanceOf(GoogleDriveConnectionException::class.java)
            .hasMessageContaining("refresh token")

        oauthServer.verify()
    }
}