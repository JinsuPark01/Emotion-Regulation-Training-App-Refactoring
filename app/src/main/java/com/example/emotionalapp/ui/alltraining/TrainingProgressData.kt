package com.example.emotionalapp.ui.alltraining

data class TrainingProgressData(
    val userDiffDays: Long = 0L,
    val countCompleteMap: Map<String, Long> = emptyMap()
)