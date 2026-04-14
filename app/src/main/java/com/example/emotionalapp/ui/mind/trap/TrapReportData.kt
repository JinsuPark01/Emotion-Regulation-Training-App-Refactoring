package com.example.emotionalapp.ui.mind.trap

class TrapReportData (
    val dateText: String = "",
    val situation: String = "",
    val thought: String = "",
    val trap: String = "",
    val alternative: String = "",

    val showValidity: Boolean = false,
    val validityAnswers: List<String> = emptyList(),

    val showAssumption: Boolean = false,
    val assumptionAnswers: List<String> = emptyList(),

    val showPerspective: Boolean = false,
    val perspectiveAnswers: List<String> = emptyList()
)