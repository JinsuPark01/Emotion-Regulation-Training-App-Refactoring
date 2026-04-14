package com.example.emotionalapp.ui.mind.trap

data class TrapReportUiState(
    val isLoading: Boolean = false,
    val reportData: TrapReportData? = null,
    val errorMessage: String? = null,
    val shouldNavigateToLogin: Boolean = false
)