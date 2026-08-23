package com.ariadne.android.ui.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Ariadne 화면에서 공통으로 사용하는 상단 작업 영역을 표시한다.
 *
 * 뒤로가기와 화면별 Action을 조합할 수 있도록 제공하며,
 * 특정 화면이나 기능의 버튼 구성을 내부에 고정하지 않는다.
 */
@Composable
fun ScreenTopBar(
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 40.dp,
                top = 8.dp,
                end = 28.dp,
                bottom = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 화면 뒤로가기 기능 제공
        onBackClick?.let { onClick ->
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // 화면별 Action 표시
        actions()
    }
}