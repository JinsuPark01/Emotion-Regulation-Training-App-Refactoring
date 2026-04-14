package com.example.emotionalapp.ui.emotion.select

data class SelectReportData(
    val mind11: List<Float?> = List(7) { null },
    val mind19: List<Float?> = List(7) { null },
    val body11: List<Float?> = List(7) { null },
    val body19: List<Float?> = List(7) { null },
    val mindLabels: List<String> = listOf("매우 안 좋음", "안 좋음", "보통", "좋음", "매우 좋음"),
    val bodyLabels: List<String> = listOf("매우 이완됨", "이완됨", "보통", "각성", "매우 각성됨")
)