package com.example.emotionalapp.ui.expression.stay

data class StayUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,

    val isFirstTraining: Boolean = false,
    val selectedEmotion: String? = null,
    val selectedTimerMillis: Long = 120_000L,

    val clarifiedEmotion: String = "",
    val moodChanged: String = "",

    val isMuted: Boolean = false,

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)