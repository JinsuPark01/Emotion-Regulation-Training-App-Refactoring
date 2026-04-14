package com.example.emotionalapp.ui.mind.art

data class ArtReportUiState(
    val isLoading: Boolean = false,
    val reportData: ArtReportData? = null,
    val errorMessage: String? = null
)