package com.ariadne.android.ui.file.model

/**
 * Ariadne 화면에서 표시할 파일 또는 폴더 정보
 *
 * Provider별 Metadata를 공통 UI 형태로 변환하여
 * Storage, 검색, 최근 파일 등 여러 화면에서 재사용한다.
 */
data class FileItemUiModel(
    val name: String,
    val description: String,
    val trailingText: String,
    val type: FileItemType
)

/**
 * 파일 목록에서 사용하는 표시 유형 정의
 *
 * 실제 MIME Type을 UI에서 필요한 대표 유형으로 변환하여
 * 공통된 파일 아이콘을 표시하기 위해 사용한다.
 */
enum class FileItemType {
    FOLDER,
    PDF,
    HWP,
    SHEET,
    DOCUMENT,
    OTHER
}