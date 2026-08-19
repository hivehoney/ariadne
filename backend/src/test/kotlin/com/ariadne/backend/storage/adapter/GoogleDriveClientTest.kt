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
import org.hamcrest.Matchers.startsWith
import org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets

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

    @Test
    fun `Google Drive 파일 목록을 조회한다`() {
        val trashedQuery = UriUtils.encodeQueryParam(
            "trashed = false",
            StandardCharsets.UTF_8,
        )

        // given
        mockServer.expect(requestTo(startsWith("https://www.googleapis.com/drive/v3/files")))
            .andExpect(header("Authorization", "Bearer access-token"))
            .andExpect(queryParam("pageSize", "1000"))
            .andExpect(queryParam("spaces", "drive"))
            .andExpect(queryParam("q", trashedQuery))
            .andRespond(
                withSuccess(
                    """
                {
                  "files": [
                    {
                      "id": "file-1",
                      "name": "resume.pdf",
                      "mimeType": "application/pdf",
                      "size": "1024",
                      "parents": ["folder-1"],
                      "createdTime": "2026-08-01T10:00:00Z",
                      "modifiedTime": "2026-08-10T12:00:00Z",
                      "webViewLink": "https://example.com/file-1"
                    },
                    {
                      "id": "folder-1",
                      "name": "documents",
                      "mimeType": "application/vnd.google-apps.folder"
                    }
                  ]
                }
                """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when
        val files = client.listFiles("access-token")

        // then
        assertThat(files).hasSize(2)

        assertThat(files[0].id).isEqualTo("file-1")
        assertThat(files[0].name).isEqualTo("resume.pdf")
        assertThat(files[0].size).isEqualTo("1024")
        assertThat(files[0].parents).containsExactly("folder-1")

        assertThat(files[1].id).isEqualTo("folder-1")
        assertThat(files[1].mimeType).isEqualTo("application/vnd.google-apps.folder")

        mockServer.verify()
    }

    @Test
    fun `다음 페이지가 있으면 모든 Google Drive 파일을 조회한다`() {
        // given
        mockServer.expect(requestTo(startsWith("https://www.googleapis.com/drive/v3/files")))
            .andExpect(queryParam("pageSize", "1000"))
            .andRespond(
                withSuccess(
                    """
                {
                  "files": [
                    {
                      "id": "file-1",
                      "name": "first.pdf",
                      "mimeType": "application/pdf"
                    }
                  ],
                  "nextPageToken": "next-page"
                }
                """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        mockServer.expect(requestTo(startsWith("https://www.googleapis.com/drive/v3/files")))
            .andExpect(queryParam("pageToken", "next-page"))
            .andRespond(
                withSuccess(
                    """
                {
                  "files": [
                    {
                      "id": "file-2",
                      "name": "second.pdf",
                      "mimeType": "application/pdf"
                    }
                  ]
                }
                """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // when
        val files = client.listFiles("access-token")

        // then
        assertThat(files).extracting<String> { it.id }
            .containsExactly("file-1", "file-2")

        mockServer.verify()
    }
}