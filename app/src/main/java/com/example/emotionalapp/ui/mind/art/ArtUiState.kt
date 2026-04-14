package com.example.emotionalapp.ui.mind.art

data class ArtUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 9,

    val selectedImages: List<Int> = emptyList(),
    val selectedImageIndices: List<Int> = emptyList(),
    val selectedImageResourceIds: List<Int> = emptyList(),

    // 2개 이미지 × 각 5문항
    val userAnswers: List<List<String>> = listOf(
        List(5) { "" },
        List(5) { "" }
    ),

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)