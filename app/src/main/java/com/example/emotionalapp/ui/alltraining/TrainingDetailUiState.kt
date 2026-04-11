package com.example.emotionalapp.ui.alltraining

import com.example.emotionalapp.data.DetailTrainingItem

enum class TrainingDetailTab {
    TRAINING,
    RECORD
}

data class TrainingDetailUiState(
    val pageTitle: String = "",
    val selectedTab: TrainingDetailTab = TrainingDetailTab.TRAINING,
    val recordItems: List<DetailTrainingItem> = emptyList(),
    val trainingItems: List<DetailTrainingItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)