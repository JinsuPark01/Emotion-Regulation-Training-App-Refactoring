package com.example.emotionalapp.ui.weekly

data class WeeklyReportData(
    val dateText: String = "",
    val phq9Sum: Int = -1,
    val gad7Sum: Int = -1,
    val positiveSum: Int = -1,
    val negativeSum: Int = -1
)