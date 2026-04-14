package com.example.emotionalapp.ui.expression.alternative

data class AlternativeReportUiState(
    val isLoading: Boolean = false,
    val reportData: AlternativeReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)