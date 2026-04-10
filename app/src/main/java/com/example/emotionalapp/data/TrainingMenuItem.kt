package com.example.emotionalapp.data

import androidx.annotation.ColorRes

data class TrainingMenuItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: TrainingMenuType,
    @ColorRes val backgroundColorResId: Int? = null
)