package com.example.emotionalapp.ui.expression

data class AlternativeUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 5,

    val situation: String = "",
    val selectedEmotion: String = "",
    val selectedDetailedEmotion: String = "",
    val selectedAlternative: String = "",
    val customAlternative: String = "",
    val finalActionTaken: String = "",
    val customEmotion: String = "",

    val selectedDetailedEmotionPosition: Int = -1,
    val selectedAlternativePosition: Int = -1,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)