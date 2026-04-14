package com.example.emotionalapp.ui.emotion.select

data class SelectUiState(
    val selectedMind: Int = -1,
    val selectedBody: Int = -1,

    val isWhatIsExpanded: Boolean = false,
    val isHowToExpanded: Boolean = false,
    val isCautionExpanded: Boolean = false,

    val isSelectButtonEnabled: Boolean = false,
    val selectButtonText: String = "상태 기록하기",

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)