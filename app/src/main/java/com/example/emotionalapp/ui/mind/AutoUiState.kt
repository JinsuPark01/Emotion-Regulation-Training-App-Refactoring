package com.example.emotionalapp.ui.mind

data class AutoUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,

    val answerList: List<String> = List(5) { "" },

    val selectedTrapIndex: Int = -1,
    val selectedTrapText: String = "",

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)