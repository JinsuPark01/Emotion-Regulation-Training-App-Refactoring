package com.example.emotionalapp.ui.weekly

data class WeeklyUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,

    val phq9Selections: List<Int> = List(9) { -1 },
    val gad7Selections: List<Int> = List(7) { -1 },
    val panasSelections: List<Int> = List(20) { -1 },

    val phq9Sum: Int = 0,
    val gad7Sum: Int = 0,
    val panasPositiveSum: Int = 0,
    val panasNegativeSum: Int = 0,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)