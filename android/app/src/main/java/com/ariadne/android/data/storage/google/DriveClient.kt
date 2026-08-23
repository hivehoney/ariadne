package com.ariadne.android.data.storage.google

import com.ariadne.android.data.storage.StorageClient
import com.ariadne.android.data.storage.model.StorageSnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

/**
 * Google Drive API 기반 StorageClient 구현
 *
 * Google Drive API 호출을 내부에서 처리하고,
 * 상위 계층에는 공통 StorageSnapshot만 제공한다.
 */
class DriveClient( private val accessToken: String ) : StorageClient {

    // Google Drive REST API 관리
    private val api: DriveApi = Retrofit.Builder()
        .baseUrl(DRIVE_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DriveApi::class.java)

    // Google Drive 루트 데이터 조회
    override suspend fun loadRoot(): StorageSnapshot = coroutineScope {
        // Google 계정 정보와 파일 목록 동시 조회
        val aboutDeferred = async {
            api.getAbout(
                authorization = bearerToken(),
                fields = ABOUT_FIELDS
            )
        }

        val filesDeferred = async {
            loadFiles()
        }

        DriveMapper.toSnapshot(
            about = aboutDeferred.await(),
            files = filesDeferred.await()
        )
    }

    // Google Drive 첫 번째 루트 파일 목록 조회
    private suspend fun loadFiles(): List<DriveFileDto> {
        val response = api.getFiles(
            authorization = bearerToken(),
            query = ROOT_QUERY,
            fields = FILE_FIELDS,
            spaces = DRIVE_SPACE,
            pageSize = PAGE_SIZE,
            pageToken = null
        )

        return response.files.sortedWith(
            compareBy<DriveFileDto>(
                { if (it.mimeType == FOLDER_MIME_TYPE) 0 else 1 },
                { it.name.lowercase(Locale.getDefault()) }
            )
        )
    }

    // Google Bearer Token 생성
    private fun bearerToken(): String {
        return "Bearer $accessToken"
    }

    companion object {

        // Google Drive REST API 주소
        private const val DRIVE_API_URL = "https://www.googleapis.com/drive/v3/"
        // Google 계정 조회 필드
        private const val ABOUT_FIELDS = "user(displayName,emailAddress),storageQuota(limit,usage)"
        // Google 루트 파일 조회 조건
        private const val ROOT_QUERY = "'root' in parents and trashed = false"
        // Google 파일 조회 필드
        private const val FILE_FIELDS = "nextPageToken,files(id,name,mimeType,size,modifiedTime)"
        // Google Drive 조회 공간
        private const val DRIVE_SPACE = "drive"
        // Google Drive 페이지 조회 크기
        private const val PAGE_SIZE = 100
        // Google Drive 폴더 MIME Type
        private const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"
    }
}