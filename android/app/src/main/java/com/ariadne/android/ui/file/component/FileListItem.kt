package com.ariadne.android.ui.file.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.file.model.FileItemType
import com.ariadne.android.ui.file.model.FileItemUiModel

// 파일 또는 폴더 한 항목 표시
@Composable
fun FileListItem(
    file: FileItemUiModel,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 20.dp,
                top = 11.dp,
                end = 16.dp,
                bottom = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileIcon(
            type = file.type
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = file.name,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = file.description,
                modifier = Modifier.padding(top = 2.dp),
                color = Color(0xFF929297),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = file.trailingText,
            modifier = Modifier.padding(start = 8.dp),
            color = Color(0xFFA0A0A5),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// 파일 유형별 아이콘 표시
@Composable
private fun FileIcon(
    type: FileItemType
) {
    if (type == FileItemType.FOLDER) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(48.dp)
        )

        return
    }

    // 파일 유형별 아이콘 배경색 결정
    val backgroundColor = when (type) {
        FileItemType.PDF -> Color(0xFFF5F5F5)
        FileItemType.HWP -> Color(0xFF00A7E8)
        FileItemType.SHEET -> Color(0xFF18884A)
        FileItemType.DOCUMENT -> Color(0xFF2463B5)
        FileItemType.OTHER -> Color(0xFF5A5A5E)
        FileItemType.FOLDER -> Color.Transparent
    }

    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = if (type == FileItemType.PDF) {
                    Color(0xFFDADADA)
                } else {
                    Color.White
                },
                modifier = Modifier.size(26.dp)
            )
        }
    }
}