package com.ariadne.android.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.theme.AriadneTheme

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val recentSearches = remember {
        mutableStateListOf("자소서", "토스", "이력서")
    }

    val fakeFiles = remember {
        listOf(
            SearchFileUiModel("토스_자기소개서.docx", "Google Drive", "2시간 전"),
            SearchFileUiModel("토스_이력서.pdf", "Home PC", "어제"),
            SearchFileUiModel("토스_면접정리.pdf", "OneDrive", "3일 전"),
            SearchFileUiModel("자료구조_중간정리.pdf", "Google Drive", "5일 전"),
            SearchFileUiModel("Ariadne_개발정의서.docx", "Home PC", "1주 전")
        )
    }

    val filteredFiles = if (query.isBlank()) {
        emptyList()
    } else {
        fakeFiles.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.storage.contains(query, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = 140.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.White
                    )
                }
            }

            item {
                FilterSection()
            }

            if (query.isBlank()) {
                item {
                    RecentSearchCard(
                        recentSearches = recentSearches,
                        onDeleteAll = { recentSearches.clear() },
                        onDeleteOne = { keyword -> recentSearches.remove(keyword) },
                        onKeywordClick = { keyword -> query = keyword }
                    )
                }
            } else {
                item {
                    Text(
                        text = "검색 결과 ${filteredFiles.size}개",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(filteredFiles) { file ->
                    SearchResultRow(file = file)
                }
            }
        }

        BottomSearchBar(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .imePadding(),
            focusRequester = focusRequester
        )
    }
}

@Composable
private fun FilterSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "필터",
            modifier = Modifier.weight(1f),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "필터 펼치기",
            tint = Color.White
        )
    }
}

@Composable
private fun RecentSearchCard(
    recentSearches: List<String>,
    onDeleteAll: () -> Unit,
    onDeleteOne: (String) -> Unit,
    onKeywordClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1F1F23)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "최근 검색",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFBDBDC2),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "모두 삭제",
                    color = Color(0xFF76A8FF),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onDeleteAll() }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recentSearches.forEach { keyword ->
                    SearchKeywordChip(
                        keyword = keyword,
                        onClick = { onKeywordClick(keyword) },
                        onDelete = { onDeleteOne(keyword) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchKeywordChip(
    keyword: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF34343A),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = keyword,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Surface(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(28.dp)
                    .clickable { onDelete() },
                shape = CircleShape,
                color = Color(0xFF4A4A50)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "삭제",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(file: SearchFileUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = file.name,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${file.storage} · ${file.modifiedAt}",
                    color = Color(0xFF9E9EA4),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFF2A2A2F))
    }
}

@Composable
private fun BottomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFF2B2B30)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = "검색",
                            color = Color(0xFFA7A7AD),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            )

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "음성 검색",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp)
            )

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp)
            )
        }
    }
}

private data class SearchFileUiModel(
    val name: String,
    val storage: String,
    val modifiedAt: String
)

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    AriadneTheme {
        SearchScreen(onBackClick = {})
    }
}