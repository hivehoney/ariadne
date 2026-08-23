package com.ariadne.android.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.common.component.BreadcrumbBar
import com.ariadne.android.ui.common.component.ConnectionInfoHeader
import com.ariadne.android.ui.common.component.ScreenTopBar
import com.ariadne.android.ui.common.component.SortBar
import com.ariadne.android.ui.common.model.BreadcrumbItem
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel
import com.ariadne.android.ui.file.component.FileListItem
import com.ariadne.android.ui.file.model.FileItemUiModel

/**
 * Storage의 파일 탐색 화면 전체 구성 담당
 *
 * 계정 정보부터 파일 목록까지 하나의 스크롤 영역으로 구성하며,
 * 공통 UI 컴포넌트를 조합하여 Provider에 관계없는 탐색 화면을 제공한다.
 */
@Composable
fun StorageScreen(
    storageName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    files: List<FileItemUiModel> = emptyList(),
    connectionInfo: ConnectionInfoUiModel? = null,
    isLoading: Boolean = false,
    sortLabel: String = "이름",
    ascending: Boolean = true,
    onSearchClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onFileClick: (FileItemUiModel) -> Unit = {},
    onDisconnectClick: () -> Unit = {}
) {
    // Storage 최초 화면을 파일 영역으로 설정
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = 1
    )

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 외부 Storage 연결 정보 표시
        item {
            if (connectionInfo != null) {
                ConnectionInfoHeader(
                    info = connectionInfo,
                    onAccountClick = onAccountClick,
                    onDisconnectClick = onDisconnectClick
                )
            }
        }

        // Storage 상단 작업 영역 표시
        item {
            ScreenTopBar(
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = onSearchClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = onMoreClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "더보기",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }

        // 현재 Storage 탐색 경로 표시
        item {
            BreadcrumbBar(
                items = listOf(
                    BreadcrumbItem(
                        label = storageName
                    )
                )
            )
        }

        // 파일 목록 정렬 상태 표시
        item {
            SortBar(
                label = sortLabel,
                ascending = ascending,
                onClick = onSortClick
            )
        }

        if (isLoading) {
            // Storage 데이터 조회 진행 상태 표시
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } else {
            // Storage 파일 목록 표시
            itemsIndexed(files) { index, file ->
                FileListItem(
                    file = file,
                    onClick = {
                        onFileClick(file)
                    }
                )

                if (index != files.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = 84.dp,
                            end = 16.dp
                        ),
                        color = Color(0xFF303034)
                    )
                }
            }
        }
    }
}
