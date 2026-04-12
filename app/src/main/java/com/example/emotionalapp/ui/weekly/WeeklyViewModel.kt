package com.example.emotionalapp.ui.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeeklyViewModel(
    private val repository: WeeklyRepository = WeeklyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyUiState())
    val uiState: StateFlow<WeeklyUiState> = _uiState.asStateFlow()

    fun goPrevPage() {
        val current = _uiState.value.currentPage
        if (current > 0 && current != 3) {
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

    fun selectPhq9(questionIndex: Int, optionIndex: Int) {
        val updated = _uiState.value.phq9Selections.toMutableList()
        if (questionIndex in updated.indices) {
            updated[questionIndex] = optionIndex
            _uiState.value = _uiState.value.copy(phq9Selections = updated)
        }
    }

    fun selectGad7(questionIndex: Int, optionIndex: Int) {
        val updated = _uiState.value.gad7Selections.toMutableList()
        if (questionIndex in updated.indices) {
            updated[questionIndex] = optionIndex
            _uiState.value = _uiState.value.copy(gad7Selections = updated)
        }
    }

    fun selectPanas(questionIndex: Int, optionIndex: Int) {
        val updated = _uiState.value.panasSelections.toMutableList()
        if (questionIndex in updated.indices) {
            updated[questionIndex] = optionIndex
            _uiState.value = _uiState.value.copy(panasSelections = updated)
        }
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value

        return when (state.currentPage) {
            0 -> {
                val unanswered = state.phq9Selections.indexOfFirst { it == -1 }
                if (unanswered != -1) "${unanswered + 1}번 질문에 답해주세요." else null
            }
            1 -> {
                val unanswered = state.gad7Selections.indexOfFirst { it == -1 }
                if (unanswered != -1) "${unanswered + 1}번 질문에 답해주세요." else null
            }
            2 -> {
                val unanswered = state.panasSelections.indexOfFirst { it == -1 }
                if (unanswered != -1) "${unanswered + 1}번 질문에 답해주세요." else null
            }
            else -> null
        }
    }

    fun calculateCurrentPageScores() {
        val state = _uiState.value

        when (state.currentPage) {
            0 -> {
                _uiState.value = state.copy(
                    phq9Sum = state.phq9Selections.sum()
                )
            }
            1 -> {
                _uiState.value = state.copy(
                    gad7Sum = state.gad7Selections.sum()
                )
            }
            2 -> {
                val positiveIndices = listOf(0, 3, 4, 7, 8, 11, 13, 16, 17, 18)
                val negativeIndices = listOf(1, 2, 5, 6, 9, 10, 12, 14, 15, 19)

                val positiveSum = positiveIndices.sumOf { state.panasSelections[it] + 1 }
                val negativeSum = negativeIndices.sumOf { state.panasSelections[it] + 1 }

                _uiState.value = state.copy(
                    panasPositiveSum = positiveSum,
                    panasNegativeSum = negativeSum
                )
            }
        }
    }

    fun shouldSaveAtCurrentPage(): Boolean {
        return _uiState.value.currentPage == 2
    }

    fun saveTraining() {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveWeeklyTraining(_uiState.value)

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
                        errorMessage = e.message ?: "저장 실패. 다시 시도해주세요."
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

    fun interpretPhq9(score: Int): String = when {
        score <= 4 -> "정상입니다. 적응 상 어려움을 초래할만한 우울관련 증상을 거의 보고하지 않았습니다."
        score <= 9 -> "경미한 수준입니다. 약간의 우울감이 있으나 일상생활에 지장을 줄 정도는 아닙니다."
        score <= 14 -> "중간 수준의 우울감입니다. 2주 연속 지속될 경우 일상생활(직업적, 사회적)에 다소 영향을 미칠 수 있어 관심이 필요합니다."
        score <= 19 -> "약간 심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)에 영향을 미칠 경우, 정신건강전문가의 도움을 받아보세요."
        else -> "심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)의 다양한 영역에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretGad7(score: Int): String = when {
        score <= 4 -> "정상입니다. 주의가 필요할 정도의 불안을 보고하지 않았습니다."
        score <= 9 -> "다소 경미한 수준의 걱정과 불안을 경험하는 것으로 보입니다."
        score <= 14 -> "주의가 필요한 수준의 과도한 걱정과 불안을 보고하였습니다. 2주 연속 지속될 경우 정신건강전문가의 도움을 받아보세요."
        else -> "과도하고 심한 걱정과 불안을 보고하였습니다. 2주 연속 지속되며 일상생활에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretPanas(pa: Int, na: Int): String = when {
        pa > na -> "긍정 감정 우세"
        pa < na -> "부정 감정 우세"
        else -> "긍·부정 감정 균형"
    }
}