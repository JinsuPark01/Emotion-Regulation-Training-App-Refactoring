package com.example.emotionalapp.ui.expression.avoidance

data class AvoidanceReportUiState(
    val isLoading: Boolean = false,
    val reportData: AvoidanceReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)