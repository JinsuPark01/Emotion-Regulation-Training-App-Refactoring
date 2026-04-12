package com.example.emotionalapp.ui.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArcViewModel(
    private val repository: ArcRepository = ArcRepository()
) : ViewModel() {

    val optionsQ1 = listOf(
        "그 반응이 최선이었던 것 같아요",
        "다른 반응도 가능했겠다는 생각이 들어요",
        "잘 모르겠어요"
    )

    val optionsQ2 = listOf(
        "전보다 나아졌어요",
        "비슷한 거 같아요",
        "더 안 좋아졌어요"
    )

    private val _uiState = MutableStateFlow(ArcUiState())
    val uiState: StateFlow<ArcUiState> = _uiState.asStateFlow()

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

    fun updateAntecedent(text: String) {
        _uiState.value = _uiState.value.copy(userAntecedent = text)
    }

    fun updateResponse(text: String) {
        _uiState.value = _uiState.value.copy(userResponse = text)
    }

    fun updateShortConsequence(text: String) {
        _uiState.value = _uiState.value.copy(userShortConsequence = text)
    }

    fun updateLongConsequence(text: String) {
        _uiState.value = _uiState.value.copy(userLongConsequence = text)
    }

    fun selectQ1(index: Int) {
        _uiState.value = _uiState.value.copy(selectedQ1Index = index)
    }

    fun selectQ2(index: Int) {
        _uiState.value = _uiState.value.copy(selectedQ2Index = index)
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value

        return when (state.currentPage) {
            1 -> {
                if (state.userAntecedent.isBlank()) "모든 질문에 답변해주세요." else null
            }
            2 -> {
                if (state.userResponse.isBlank()) "모든 질문에 답변해주세요." else null
            }
            3 -> {
                if (state.userShortConsequence.isBlank() || state.userLongConsequence.isBlank()) {
                    "모든 질문에 답변해주세요."
                } else null
            }
            4 -> {
                if (state.selectedQ1Index == -1 || state.selectedQ2Index == -1) {
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

            val result = repository.saveArcTraining(
                state = _uiState.value,
                optionsQ1 = optionsQ1,
                optionsQ2 = optionsQ2
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