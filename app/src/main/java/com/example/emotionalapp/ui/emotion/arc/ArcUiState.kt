package com.example.emotionalapp.ui.emotion.arc

data class ArcUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 5,

    val userAntecedent: String = "",
    val userResponse: String = "",
    val userShortConsequence: String = "",
    val userLongConsequence: String = "",

    val selectedQ1Index: Int = -1,
    val selectedQ2Index: Int = -1,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)