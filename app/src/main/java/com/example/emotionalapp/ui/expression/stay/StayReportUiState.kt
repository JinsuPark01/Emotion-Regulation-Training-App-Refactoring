package com.example.emotionalapp.ui.expression.stay

data class StayReportUiState(
    val isLoading: Boolean = false,
    val reportData: StayReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)