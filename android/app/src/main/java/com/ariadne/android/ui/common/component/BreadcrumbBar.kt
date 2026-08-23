package com.ariadne.android.ui.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.common.model.BreadcrumbItem

// 현재 탐색 경로 표시
@Composable
fun BreadcrumbBar(
    items: List<BreadcrumbItem>,
    onItemClick: (Int) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF202022)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(
                    horizontal = 18.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "홈",
                tint = Color(0xFFD0D0D4),
                modifier = Modifier.size(22.dp)
            )

            items.forEachIndexed { index, item ->
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF929297),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(20.dp)
                )

                // 현재 경로 마지막 항목 강조
                Text(
                    text = item.label,
                    modifier = Modifier.clickable {
                        onItemClick(index)
                    },
                    color = if (index == items.lastIndex) {
                        Color(0xFF7AA2FF)
                    } else {
                        Color(0xFFD0D0D4)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (index == items.lastIndex) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }
        }
    }
}