package com.example.emotionalapp.ui.expression.avoidance

data class AvoidanceUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 2,

    val selectedAvoidanceIndexes: Set<Int> = emptySet(),
    val customAvoidance: String = "",
    val effect: String = "",

    val situation: String = "",
    val emotion: String = "",
    val method: String = "",
    val result: String = "",

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)