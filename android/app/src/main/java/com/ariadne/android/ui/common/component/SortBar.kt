package com.ariadne.android.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 목록 정렬 상태 표시
@Composable
fun SortBar(
    label: String,
    ascending: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 20.dp,
                top = 8.dp,
                end = 18.dp,
                bottom = 4.dp
            ),
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
            text = label,
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
            contentDescription = if (ascending) {
                "오름차순"
            } else {
                "내림차순"
            },
            tint = Color.White,
            modifier = Modifier
                .size(22.dp)
                .rotate(
                    if (ascending) {
                        0f
                    } else {
                        180f
                    }
                )
        )
    }
}