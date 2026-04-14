package com.example.emotionalapp.ui.emotion.select

data class SelectReportUiState(
    val isLoading: Boolean = false,
    val reportData: SelectReportData? = null,
    val errorMessage: String? = null
)