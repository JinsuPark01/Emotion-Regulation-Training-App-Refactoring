package com.example.emotionalapp.ui.emotion.arc

data class ArcReportUiState(
    val isLoading: Boolean = false,
    val reportData: ArcReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)