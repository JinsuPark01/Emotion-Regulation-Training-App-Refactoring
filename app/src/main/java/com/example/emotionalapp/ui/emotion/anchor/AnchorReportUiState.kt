package com.example.emotionalapp.ui.emotion.anchor

data class AnchorReportUiState(
    val isLoading: Boolean = false,
    val reportData: AnchorReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)