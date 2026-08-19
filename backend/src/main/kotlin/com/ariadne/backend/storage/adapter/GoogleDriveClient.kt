package com.ariadne.backend.storage.adapter

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

/**
 * Google Drive API 호출을 담당한다.
 *
 * 현재는 연결 검증을 위해 사용자 정보를 조회하고,
 * 이후 Metadata Sync 단계에서 파일 목록 조회 기능을 추가한다.
 */
@Component
class GoogleDriveClient(
    restClientBuilder: RestClient.Builder,
) {
    private val restClient = restClientBuilder
        .baseUrl(GOOGLE_DRIVE_BASE_URL)
        .build()

    fun getCurrentUser(accessToken: String): GoogleDriveUser {
        require(accessToken.isNotBlank()) {
            "Google access token must not be blank."
        }

        return try {
            val response = restClient.get()
                .uri { builder ->
                    builder.path("/about")
                        .queryParam("fields", "user(displayName,emailAddress,permissionId)")
                        .build()
                }
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .body(GoogleDriveAboutResponse::class.java)
                ?: throw GoogleDriveApiException("Google Drive user response is empty.")

            response.user
        } catch (exception: RestClientException) {
            throw GoogleDriveApiException(
                "Failed to get Google Drive user information.",
                exception,
            )
        }
    }

    fun listFiles(accessToken: String): List<GoogleDriveFileMetadata> {
        require(accessToken.isNotBlank()) {
            "Google access token must not be blank."
        }

        val files = mutableListOf<GoogleDriveFileMetadata>()
        var pageToken: String? = null

        do {
            val response = listFilesPage(accessToken, pageToken)

            if (response.incompleteSearch) {
                throw GoogleDriveApiException("Google Drive file search was incomplete.")
            }

            files.addAll(response.files)
            pageToken = response.nextPageToken?.takeIf { it.isNotBlank() }
        } while (pageToken != null)

        return files
    }

    private fun listFilesPage(
        accessToken: String,
        pageToken: String?,
    ): GoogleDriveFileListResponse {
        return try {
            restClient.get()
                .uri { builder ->
                    builder.path("/files")
                        .queryParam("pageSize", MAX_PAGE_SIZE)
                        .queryParam("spaces", "drive")
                        .queryParam("q", "trashed = false")
                        .queryParam("fields", FILE_FIELDS)
                        .apply {
                            if (pageToken != null) {
                                queryParam("pageToken", pageToken)
                            }
                        }
                        .build()
                }
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .body(GoogleDriveFileListResponse::class.java)?: throw GoogleDriveApiException("Google Drive file list response is empty.")
        } catch (exception: GoogleDriveApiException) {
            throw exception
        } catch (exception: RestClientException) {
            throw GoogleDriveApiException(
                "Failed to get Google Drive file list.",
                exception,
            )
        }
    }

    companion object {
        private const val GOOGLE_DRIVE_BASE_URL = "https://www.googleapis.com/drive/v3"
        private const val MAX_PAGE_SIZE = 1000

        private const val FILE_FIELDS =
            "nextPageToken,incompleteSearch," +
                    "files(id,name,mimeType,size,parents,createdTime,modifiedTime,webViewLink)"
    }
}

data class GoogleDriveUser(
    val displayName: String,
    val emailAddress: String? = null,
    val permissionId: String,
)

private data class GoogleDriveAboutResponse(
    val user: GoogleDriveUser,
)