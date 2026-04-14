package com.example.emotionalapp.ui.mind.art

data class ArtReportData(
    val firstImageName: String = "",
    val secondImageName: String = "",
    val firstAnswers: List<String> = emptyList(),
    val secondAnswers: List<String> = emptyList()
)