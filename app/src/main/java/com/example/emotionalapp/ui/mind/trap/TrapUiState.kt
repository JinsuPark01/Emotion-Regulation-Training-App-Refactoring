package com.example.emotionalapp.ui.mind.trap

data class TrapUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 8,

    val responsePage1Answer1: String = "",
    val responsePage1Answer2: String = "",

    val selectedPage2Index: Int = -1,
    val responsePage2Text: String = "",

    val selectedTrapIndex: Int = -1,

    val responsePage4ZeroAnswers: List<String> = List(4) { "" },
    val responsePage4OneAnswers: List<String> = List(4) { "" },
    val responsePage4TwoAnswers: List<String> = List(3) { "" },

    val responsePage6Text: String = "",

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)