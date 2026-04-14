package com.example.emotionalapp.ui.mind.auto

data class AutoReportUiState(
    val isLoading: Boolean = false,
    val reportData: AutoReportData? = null,
    val errorMessage: String? = null
)