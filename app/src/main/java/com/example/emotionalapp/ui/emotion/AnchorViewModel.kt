package com.example.emotionalapp.ui.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnchorViewModel(
    private val repository: AnchorRepository = AnchorRepository()
) : ViewModel() {

    val cueOptions = listOf(
        "숨소리에 집중하기",
        "심장 박동 8번 느껴보기",
        "'옴~'소리를 5초간 내어보기"
    )

    val optionsQ1 = listOf(
        "현재에 집중할 수 있었어요",
        "다른 단서를 찾아봐야 할 것 같아요"
    )

    val optionsQ2 = listOf(
        "전보다 나아졌어요",
        "비슷한 거 같아요",
        "더 안 좋아졌어요"
    )

    private val _uiState = MutableStateFlow(AnchorUiState())
    val uiState: StateFlow<AnchorUiState> = _uiState.asStateFlow()

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

    fun selectCue(index: Int) {
        _uiState.value = _uiState.value.copy(
            selectedCueIndex = index,
            customCueInput = "",
            selectedCue = cueOptions.getOrElse(index) { "" }
        )
    }

    fun updateCustomCueInput(text: String) {
        _uiState.value = _uiState.value.copy(
            customCueInput = text,
            selectedCueIndex = -1,
            selectedCue = text
        )
    }

    fun updatePage2Answer1(text: String) {
        _uiState.value = _uiState.value.copy(page2Answer1 = text)
    }

    fun updatePage2Answer2(text: String) {
        _uiState.value = _uiState.value.copy(page2Answer2 = text)
    }

    fun updatePage2Answer3(text: String) {
        _uiState.value = _uiState.value.copy(page2Answer3 = text)
    }

    fun selectQ1(index: Int) {
        val answer = optionsQ1.getOrElse(index) { "" }
        _uiState.value = _uiState.value.copy(
            selectedQ1Index = index,
            page3Answer1 = answer
        )
    }

    fun selectQ2(index: Int) {
        val answer = optionsQ2.getOrElse(index) { "" }
        _uiState.value = _uiState.value.copy(
            selectedQ2Index = index,
            page3Answer2 = answer
        )
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value
        return when (state.currentPage) {
            1 -> {
                val hasPreset = state.selectedCueIndex in cueOptions.indices
                val hasCustom = state.customCueInput.isNotBlank()
                if (!hasPreset && !hasCustom) "단서를 선택하거나 입력해주세요." else null
            }
            2 -> {
                if (
                    state.page2Answer1.isBlank() ||
                    state.page2Answer2.isBlank() ||
                    state.page2Answer3.isBlank()
                ) {
                    "모든 질문에 답변해주세요."
                } else null
            }
            3 -> {
                if (state.selectedQ1Index == -1 || state.selectedQ2Index == -1) {
                    "두 질문 모두 답변해주세요."
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

            val result = repository.saveAnchorTraining(
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