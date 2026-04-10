package com.example.emotionalapp.ui.alltraining

import com.example.emotionalapp.data.DetailTrainingItem

data class TrainingDetailUiState(
    val pageTitle: String = "",
    val recordItems: List<DetailTrainingItem> = emptyList(),
    val trainingItems: List<DetailTrainingItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)