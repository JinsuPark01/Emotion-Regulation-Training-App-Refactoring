package com.example.emotionalapp.ui.expression.opposite

data class OppositeUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,

    val answer1: String = "",
    val answer2: String = "",
    val answer3: String = "",
    val answer5: String = "",

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)