package com.example.emotionalapp.ui.mind

data class AutoTrapReportUiState(
    val isLoading: Boolean = false,
    val reportData: AutoTrapReportData? = null,
    val errorMessage: String? = null
)