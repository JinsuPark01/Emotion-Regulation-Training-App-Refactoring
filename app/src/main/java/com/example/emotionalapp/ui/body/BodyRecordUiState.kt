package com.example.emotionalapp.ui.body

data class BodyRecordUiState(
    val trainingId: String = "",
    val feedbackText: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)