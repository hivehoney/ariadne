package com.ariadne.android.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.theme.AriadneTheme

@Composable
fun StorageScreen(
    storageName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    val files = listOf(
        StorageItemUiModel(
            name = "Colab Notebooks",
            description = "2023년 11월 9일 오후 8:46",
            trailingText = "13개",
            type = StorageItemType.FOLDER
        ),
        StorageItemUiModel(
            name = "1학년 사회 수행평가 안내(2016.11월 수정).hwp",
            description = "2016년 11월 16일 오전 12:15",
            trailingText = "32.77 KB",
            type = StorageItemType.HWP
        ),
        StorageItemUiModel(
            name = "9.14 1주차 연습문제.pdf",
            description = "2022년 10월 24일 오전 4:44",
            trailingText = "543 KB",
            type = StorageItemType.PDF
        ),
        StorageItemUiModel(
            name = "구글시트_자산관리포트폴리오_v2.1 공유용의 사본",
            description = "2025년 2월 25일 오전 10:02",
            trailingText = "567 KB",
            type = StorageItemType.SHEET
        ),
        StorageItemUiModel(
            name = "면접정리",
            description = "2024년 3월 29일 오후 9:19",
            trailingText = "8.59 KB",
            type = StorageItemType.DOCUMENT
        ),
        StorageItemUiModel(
            name = "면접질문",
            description = "2024년 3월 29일 오후 9:20",
            trailingText = "7.83 KB",
            type = StorageItemType.DOCUMENT
        ),
        StorageItemUiModel(
            name = "부동산특약",
            description = "2025년 4월 19일 오후 2:44",
            trailingText = "1.37 KB",
            type = StorageItemType.DOCUMENT
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        StorageTopBar(
            onBackClick = onBackClick,
            onSearchClick = onSearchClick
        )

        Breadcrumb(storageName = storageName)

        SortBar()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(files) { index, file ->
                StorageFileRow(file = file)

                if (index != files.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 84.dp, end = 16.dp),
                        color = Color(0xFF303034)
                    )
                }
            }
        }
    }
}

@Composable
private fun StorageTopBar(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color(0xFF202022)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF202022)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Breadcrumb(storageName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF202022)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "홈",
                tint = Color(0xFFD0D0D4),
                modifier = Modifier.size(22.dp)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF929297),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(20.dp)
            )

            Text(
                text = storageName,
                color = Color(0xFF7AA2FF),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SortBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 8.dp, end = 18.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Sort,
            contentDescription = "정렬",
            tint = Color(0xFFD2D2D6),
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = "이름",
            modifier = Modifier.padding(start = 10.dp),
            color = Color(0xFFD2D2D6),
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .width(1.dp)
                .height(24.dp)
                .background(Color(0xFF57575B))
        )

        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = "오름차순",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun StorageFileRow(file: StorageItemUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(start = 20.dp, top = 11.dp, end = 16.dp, bottom = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileIcon(type = file.type)

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

@Composable
private fun FileIcon(type: StorageItemType) {
    when (type) {
        StorageItemType.FOLDER -> {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(48.dp)
            )
        }

        else -> {
            val backgroundColor = when (type) {
                StorageItemType.PDF -> Color(0xFFF5F5F5)
                StorageItemType.HWP -> Color(0xFF00A7E8)
                StorageItemType.SHEET -> Color(0xFF18884A)
                StorageItemType.DOCUMENT -> Color(0xFF2463B5)
                StorageItemType.FOLDER -> Color.Transparent
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
                        tint = if (type == StorageItemType.PDF) {
                            Color(0xFFDADADA)
                        } else {
                            Color.White
                        },
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

private data class StorageItemUiModel(
    val name: String,
    val description: String,
    val trailingText: String,
    val type: StorageItemType
)

private enum class StorageItemType {
    FOLDER,
    PDF,
    HWP,
    SHEET,
    DOCUMENT
}

@Preview(showBackground = true)
@Composable
private fun StorageScreenPreview() {
    AriadneTheme {
        StorageScreen(
            storageName = "Google Drive",
            onBackClick = {}
        )
    }
}