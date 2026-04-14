package com.example.emotionalapp.ui.body

data class BodyReportUiState(
    val isLoading: Boolean = false,
    val reportData: BodyReportData? = null,
    val errorMessage: String? = null
)