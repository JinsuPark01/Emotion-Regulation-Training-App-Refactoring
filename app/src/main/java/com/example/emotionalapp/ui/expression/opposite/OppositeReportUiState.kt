package com.example.emotionalapp.ui.expression.opposite

data class OppositeReportUiState(
    val isLoading: Boolean = false,
    val reportData: OppositeReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)