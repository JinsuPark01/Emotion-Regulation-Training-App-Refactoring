package com.example.emotionalapp.ui.emotion

data class AnchorUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,

    val selectedCueIndex: Int = -1,
    val customCueInput: String = "",
    val selectedCue: String = "",

    val page2Answer1: String = "",
    val page2Answer2: String = "",
    val page2Answer3: String = "",

    val selectedQ1Index: Int = -1,
    val selectedQ2Index: Int = -1,
    val page3Answer1: String = "",
    val page3Answer2: String = "",

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)