package com.example.emotionalapp.ui.weekly

data class WeeklyReportUiState(
    val isLoading: Boolean = false,
    val reportData: WeeklyReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)