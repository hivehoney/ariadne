package com.ariadne.android.data.storage.google

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Google Drive REST API 호출 규격
 *
 * 사용자 및 용량 정보와 루트 파일 목록 조회에 필요한
 * Google Drive API Endpoint 정의
 */
interface DriveApi {

    // Google Drive 계정 및 용량 조회
    @GET("about")
    suspend fun getAbout(
        @Header("Authorization") authorization: String,
        @Query("fields") fields: String
    ): DriveAboutDto

    // Google Drive 파일 목록 조회
    @GET("files")
    suspend fun getFiles(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("fields") fields: String,
        @Query("spaces") spaces: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String?
    ): DriveFileListResponse
}