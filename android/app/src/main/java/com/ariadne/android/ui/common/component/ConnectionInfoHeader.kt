package com.ariadne.android.ui.common.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel

/**
 * 외부 서비스의 계정 및 연결 정보 표시
 *
 * 연결된 서비스 이름, 계정, 사용 가능 용량을 공통 형태로 표시하고
 * 계정 선택과 연결 해제 기능을 제공한다.
 */
@Composable
fun ConnectionInfoHeader(
    info: ConnectionInfoUiModel,
    onAccountClick: () -> Unit = {},
    onDisconnectClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // 연결 서비스 이름 표시
        Text(
            text = info.title,
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // 연결 계정 표시
        OutlinedButton(
            onClick = onAccountClick,
            modifier = Modifier
                .widthIn(
                    min = 150.dp,
                    max = 180.dp
                )
                .height(30.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFA7A7AC)
            ),
            contentPadding = PaddingValues(
                horizontal = 10.dp
            )
        ) {
            Text(
                text = info.account,
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "계정 선택",
                tint = Color(0xFFD0D0D4)
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Storage 사용 가능 용량 표시
        Text(
            text = info.detail,
            color = Color(0xFFA7A7AC),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        // Storage 연결 즉시 해제
        TextButton(
            onClick = onDisconnectClick,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        ) {
            Text(
                text = "연결 해제",
                color = Color(0xFF8BAAFF),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}