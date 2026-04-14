package com.example.emotionalapp.data

import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity

// 상세 훈련 어댑팅을 위한 데이터 클래스
data class DetailTrainingItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val trainingType: TrainingType,
    val progressNumerator: String,
    val progressDenominator: String,
    val currentProgress: String,
    @ColorRes val backgroundColorResId: Int? = null,
    val targetActivityClass: Class<out AppCompatActivity>? = null,
    val reportDateMillis: Long? = null,
    val trainingIdForReport: String? = null
)
