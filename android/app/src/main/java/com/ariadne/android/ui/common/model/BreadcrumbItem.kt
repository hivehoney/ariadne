package com.ariadne.android.ui.common.model

/**
 * Breadcrumb에서 표시할 하나의 경로 정보를 표현한다.
 *
 * 계층형 화면에서 현재 탐색 위치를 공통된 형태로 전달하기 위해 사용한다.
 */
data class BreadcrumbItem(
    val label: String
)