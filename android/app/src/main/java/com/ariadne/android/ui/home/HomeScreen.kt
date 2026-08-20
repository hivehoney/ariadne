package com.ariadne.android.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoFile
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.theme.AriadneTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onSearchClick: () -> Unit = {}, onStorageClick: (String) -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                HomeHeader()
            }

            item {
                CategorySection()
            }

            item {
                RecentFilesSection()
            }

            item {
                OrganizationSuggestionSection()
            }

            item {
                StorageSection(onStorageClick = onStorageClick)
            }
        }

        HomeSearchBar(
            onClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.62f)
                .padding(bottom = 18.dp)
        )
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 28.dp, end = 12.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ariadne",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기"
            )
        }
    }
}

@Composable
private fun CategorySection() {
    val categories = listOf(
        CategoryUiModel("이미지", Icons.Default.Image),
        CategoryUiModel("오디오", Icons.Default.AudioFile),
        CategoryUiModel("비디오", Icons.Default.VideoFile),
        CategoryUiModel("문서", Icons.Default.Description),
        CategoryUiModel("다운로드", Icons.Default.Download)
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "카테고리",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentFilesSection() {
    val files = listOf(
        RecentFileUiModel("이력서.pdf", "PDF"),
        RecentFileUiModel("발표자료.pptx", "PPTX"),
        RecentFileUiModel("프로젝트.docx", "DOCX"),
        RecentFileUiModel("사진.jpg", "JPG")
    )

    Column(
        modifier = Modifier.padding(top = 34.dp)
    ) {
        SectionHeader(
            title = "최근 파일",
            action = "더보기"
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(files) { file ->
                RecentFileCard(file)
            }
        }
    }
}

@Composable
private fun RecentFileCard(file: RecentFileUiModel) {
    Surface(
        modifier = Modifier
            .size(width = 128.dp, height = 146.dp)
            .clickable { },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = file.type,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OrganizationSuggestionSection() {
    Column(
        modifier = Modifier.padding(top = 34.dp)
    ) {
        SectionHeader(title = "정리 추천")

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "토스 지원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "17개 파일 · 4개 위치",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "관련 파일이 여러 저장소에 흩어져 있습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "정리 추천 보기"
                )
            }
        }
    }
}

@Composable
private fun StorageSection(onStorageClick: (String) -> Unit) {
    val storages = listOf(
        StorageUiModel("Google Drive", "연결됨", Icons.Default.Cloud),
        StorageUiModel("OneDrive", "연결하기", Icons.Default.Cloud),
        StorageUiModel("Home PC", "오프라인", Icons.Default.Computer),
        StorageUiModel("이 기기", null, Icons.Default.PhoneAndroid)
    )

    Column(
        modifier = Modifier.padding(top = 34.dp)
    ) {
        SectionHeader(title = "저장공간")

        Spacer(modifier = Modifier.height(10.dp))

        storages.forEach { storage ->
            StorageRow(
                storage = storage,
                onClick = {
                    onStorageClick(storage.name)
                }
            )
        }
    }
}

@Composable
private fun StorageRow(storage: StorageUiModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = storage.icon,
            contentDescription = storage.name,
            modifier = Modifier.size(28.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 18.dp)
        ) {
            Text(
                text = storage.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            storage.status?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (action != null) {
            Text(
                text = action,
                modifier = Modifier.clickable { },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HomeSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFF2E2E30)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color(0xFFB8B8BC)
            )

            Text(
                text = "검색",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9B9B9F)
            )

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "음성 검색",
                modifier = Modifier.size(26.dp),
                tint = Color.White
            )
        }
    }
}

private data class CategoryUiModel(
    val name: String,
    val icon: ImageVector
)

private data class RecentFileUiModel(
    val name: String,
    val type: String
)

private data class StorageUiModel(
    val name: String,
    val status: String?,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AriadneTheme {
        HomeScreen()
    }
}