package com.ariadne.android.ui.storage

import com.ariadne.android.data.storage.model.StorageFile
import com.ariadne.android.data.storage.model.StorageFileType
import com.ariadne.android.data.storage.model.StorageSnapshot
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel
import com.ariadne.android.ui.file.model.FileItemType
import com.ariadne.android.ui.file.model.FileItemUiModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 공통 Storage 데이터를 화면용 모델로 변환
 *
 * Provider 종류와 관계없이 동일한 StorageSnapshot을 받아
 * StorageScreen과 FileListItem에서 사용하는 UI 모델로 변환한다.
 */
object StorageUiMapper {

    // Storage 연결 정보 UI 변환
    fun toConnectionInfo( snapshot: StorageSnapshot ): ConnectionInfoUiModel {
        val connection = snapshot.connection

        return ConnectionInfoUiModel(
            title = connection.name,
            account = connection.account,
            detail = connection.availableBytes
                ?.let { "${formatBytes(it)} 사용 가능" }
                ?: ""
        )
    }

    // Storage 파일 목록 UI 변환
    fun toFileItems( snapshot: StorageSnapshot ): List<FileItemUiModel> {
        return snapshot.files.map(::toFileItem)
    }

    // Storage 파일 Metadata UI 변환
    private fun toFileItem( file: StorageFile ): FileItemUiModel {
        return FileItemUiModel(
            name = file.name,
            description = formatModifiedTime(file.modifiedAt),
            trailingText = createTrailingText(file),
            type = toUiType(file.type)
        )
    }

    // 파일 우측 정보 생성
    private fun createTrailingText( file: StorageFile ): String {
        if (file.type == StorageFileType.FOLDER) {
            return file.itemCount?.let { "${it}개" } ?: ""
        }

        return file.size?.let(::formatBytes) ?: ""
    }

    // 공통 파일 유형 UI 변환
    private fun toUiType( type: StorageFileType ): FileItemType {
        return when (type) {
            StorageFileType.FOLDER -> FileItemType.FOLDER
            StorageFileType.PDF -> FileItemType.PDF
            StorageFileType.HWP -> FileItemType.HWP
            StorageFileType.SHEET -> FileItemType.SHEET
            StorageFileType.DOCUMENT -> FileItemType.DOCUMENT
            StorageFileType.OTHER -> FileItemType.OTHER
        }
    }

    // 파일 수정 시각 화면 형식 변환
    private fun formatModifiedTime( value: String? ): String {
        if (value.isNullOrBlank()) return ""

        val date = INPUT_DATE_PATTERNS
            .firstNotNullOfOrNull { pattern ->
                runCatching {
                    SimpleDateFormat(pattern, Locale.US).parse(value)
                }.getOrNull()
            }
            ?: return value

        return SimpleDateFormat(
            "yyyy년 M월 d일 a h:mm",
            Locale.KOREA
        ).format(date)
    }

    // Byte 단위 화면 표시 형식 변환
    private fun formatBytes( bytes: Long ): String {
        val formatter = DecimalFormat("#.##")

        return when {
            bytes >= GB -> "${formatter.format(bytes.toDouble() / GB)} GB"
            bytes >= MB -> "${formatter.format(bytes.toDouble() / MB)} MB"
            bytes >= KB -> "${formatter.format(bytes.toDouble() / KB)} KB"
            else -> "$bytes B"
        }
    }

    private const val KB = 1024L
    private const val MB = KB * 1024L
    private const val GB = MB * 1024L

    private val INPUT_DATE_PATTERNS =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )
}