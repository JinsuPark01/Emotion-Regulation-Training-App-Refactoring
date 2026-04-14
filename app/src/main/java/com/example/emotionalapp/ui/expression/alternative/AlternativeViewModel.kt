package com.example.emotionalapp.ui.expression.alternative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionalapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlternativeViewModel(
    private val repository: AlternativeRepository = AlternativeRepository()
) : ViewModel() {

    val emotions = listOf(
        "화남",
        "슬픔",
        "불안",
        "외로움",
        "죄책감",
        "무기력",
        "창피함",
        "직접 입력"
    )

    val detailedEmotionResMap = mapOf(
        "화남" to R.array.detailed_emotions_anger,
        "슬픔" to R.array.detailed_emotions_sadness,
        "불안" to R.array.detailed_emotions_anxiety,
        "외로움" to R.array.detailed_emotions_loneliness,
        "죄책감" to R.array.detailed_emotions_guilt,
        "무기력" to R.array.detailed_emotions_lethargy,
        "창피함" to R.array.detailed_emotions_shame
    )

    val alternativeActionResMap = mapOf(
        "화남" to R.array.alternative_actions_anger,
        "슬픔" to R.array.alternative_actions_sadness,
        "불안" to R.array.alternative_actions_anxiety,
        "외로움" to R.array.alternative_actions_loneliness,
        "죄책감" to R.array.alternative_actions_guilt,
        "무기력" to R.array.alternative_actions_lethargy,
        "창피함" to R.array.alternative_actions_shame
    )

    private val _uiState = MutableStateFlow(AlternativeUiState())
    val uiState: StateFlow<AlternativeUiState> = _uiState.asStateFlow()

    fun goPrevPage() {
        val current = _uiState.value.currentPage
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentPage = current - 1)
        }
    }

    fun goNextPage(): Boolean {
        val current = _uiState.value.currentPage
        return if (current < _uiState.value.totalPages - 1) {
            _uiState.value = _uiState.value.copy(currentPage = current + 1)
            true
        } else {
            false
        }
    }

    fun isLastPage(): Boolean {
        return _uiState.value.currentPage == _uiState.value.totalPages - 1
    }

    fun updateSituation(text: String) {
        _uiState.value = _uiState.value.copy(situation = text)
    }

    fun updateCustomEmotion(text: String) {
        _uiState.value = _uiState.value.copy(customEmotion = text)
    }

    fun updateCustomAlternative(text: String) {
        _uiState.value = _uiState.value.copy(customAlternative = text)
    }

    fun updateFinalActionTaken(text: String) {
        _uiState.value = _uiState.value.copy(finalActionTaken = text)
    }

    fun selectEmotion(emotion: String) {
        val isChanged = _uiState.value.selectedEmotion != emotion

        _uiState.value = _uiState.value.copy(
            selectedEmotion = emotion,
            selectedDetailedEmotion = if (isChanged) "" else _uiState.value.selectedDetailedEmotion,
            selectedDetailedEmotionPosition = if (isChanged) -1 else _uiState.value.selectedDetailedEmotionPosition,
            selectedAlternative = if (isChanged) "" else _uiState.value.selectedAlternative,
            selectedAlternativePosition = if (isChanged) -1 else _uiState.value.selectedAlternativePosition,
            customEmotion = if (emotion == "직접 입력") _uiState.value.customEmotion else ""
        )
    }

    fun selectDetailedEmotion(position: Int, item: String) {
        _uiState.value = _uiState.value.copy(
            selectedDetailedEmotion = item,
            selectedDetailedEmotionPosition = position,
            customEmotion = ""
        )
    }

    fun selectAlternative(position: Int, item: String) {
        _uiState.value = _uiState.value.copy(
            selectedAlternative = item,
            selectedAlternativePosition = position
        )
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value
        return when (state.currentPage) {
            1 -> {
                when {
                    state.situation.isBlank() -> "상황을 입력해주세요."
                    state.selectedEmotion.isBlank() -> "감정을 선택해주세요."
                    state.selectedEmotion != "직접 입력" && state.selectedDetailedEmotion.isBlank() -> "세부 감정을 선택해주세요."
                    state.selectedEmotion == "직접 입력" && state.customEmotion.isBlank() -> "직접 입력 해주세요."
                    else -> null
                }
            }

            2 -> {
                if (state.selectedAlternative.isBlank() && state.customAlternative.isBlank()) {
                    "대안 행동을 선택하거나 직접 입력해주세요."
                } else null
            }

            3 -> {
                if (state.finalActionTaken.isBlank()) {
                    "어떻게 행동했는지 입력해주세요."
                } else null
            }

            else -> null
        }
    }

    fun saveTraining() {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveTraining(_uiState.value)

            result
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "저장에 실패했습니다."
                    )
                }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}