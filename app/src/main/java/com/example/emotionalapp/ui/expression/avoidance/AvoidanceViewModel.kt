package com.example.emotionalapp.ui.expression.avoidance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvoidanceViewModel(
    private val repository: AvoidanceRepository = AvoidanceRepository()
) : ViewModel() {

    val avoidanceOptions = listOf(
        "회피 행동 1",
        "회피 행동 2",
        "회피 행동 3",
        "회피 행동 4",
        "회피 행동 5",
        "회피 행동 6",
        "회피 행동 7",
        "회피 행동 8"
    )

    private val _uiState = MutableStateFlow(AvoidanceUiState())
    val uiState: StateFlow<AvoidanceUiState> = _uiState.asStateFlow()

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

    fun toggleAvoidance(index: Int) {
        val current = _uiState.value.selectedAvoidanceIndexes.toMutableSet()
        if (current.contains(index)) {
            current.remove(index)
        } else {
            current.add(index)
        }
        _uiState.value = _uiState.value.copy(selectedAvoidanceIndexes = current)
    }

    fun updateCustomAvoidance(text: String) {
        _uiState.value = _uiState.value.copy(customAvoidance = text)
    }

    fun updateEffect(text: String) {
        _uiState.value = _uiState.value.copy(effect = text)
    }

    fun updateSituation(text: String) {
        _uiState.value = _uiState.value.copy(situation = text)
    }

    fun updateEmotion(text: String) {
        _uiState.value = _uiState.value.copy(emotion = text)
    }

    fun updateMethod(text: String) {
        _uiState.value = _uiState.value.copy(method = text)
    }

    fun updateResult(text: String) {
        _uiState.value = _uiState.value.copy(result = text)
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value
        return when (state.currentPage) {
            0 -> {
                val hasChecked = state.selectedAvoidanceIndexes.isNotEmpty()
                val hasCustom = state.customAvoidance.isNotBlank()
                val hasEffect = state.effect.isNotBlank()

                if ((!hasChecked && !hasCustom) || !hasEffect) {
                    "모든 질문에 답변해주세요."
                } else null
            }
            1 -> {
                if (
                    state.situation.isBlank() ||
                    state.emotion.isBlank() ||
                    state.method.isBlank() ||
                    state.result.isBlank()
                ) {
                    "모든 질문에 답변해주세요."
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

            val selectedTexts = _uiState.value.selectedAvoidanceIndexes
                .sorted()
                .mapNotNull { avoidanceOptions.getOrNull(it) }

            val result = repository.saveAvoidanceTraining(
                state = _uiState.value,
                selectedAvoidanceTexts = selectedTexts
            )

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
}