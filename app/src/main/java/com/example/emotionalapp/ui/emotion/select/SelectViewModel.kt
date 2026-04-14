package com.example.emotionalapp.ui.emotion.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectViewModel(
    private val repository: SelectRepository = SelectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectUiState(isLoading = true))
    val uiState: StateFlow<SelectUiState> = _uiState.asStateFlow()

    fun loadInitialState() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.checkSelectableState()

            result
                .onSuccess { availability ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSelectButtonEnabled = availability.isEnabled,
                        selectButtonText = availability.buttonText
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSelectButtonEnabled = false,
                        selectButtonText = "상태 기록하기",
                        errorMessage = "기록 확인 실패: ${e.message}"
                    )
                }
        }
    }

    fun selectMind(index: Int) {
        _uiState.value = _uiState.value.copy(selectedMind = index)
    }

    fun selectBody(index: Int) {
        _uiState.value = _uiState.value.copy(selectedBody = index)
    }

    fun toggleWhatIs() {
        _uiState.value = _uiState.value.copy(
            isWhatIsExpanded = !_uiState.value.isWhatIsExpanded
        )
    }

    fun toggleHowTo() {
        _uiState.value = _uiState.value.copy(
            isHowToExpanded = !_uiState.value.isHowToExpanded
        )
    }

    fun toggleCaution() {
        _uiState.value = _uiState.value.copy(
            isCautionExpanded = !_uiState.value.isCautionExpanded
        )
    }

    fun validateSelection(): String? {
        val state = _uiState.value
        return if (state.selectedMind == -1 || state.selectedBody == -1) {
            "마음과 몸의 감정을 선택해주세요"
        } else {
            null
        }
    }

    fun saveSelection() {
        val validationError = validateSelection()
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveEmotionSelection(
                selectedMind = _uiState.value.selectedMind,
                selectedBody = _uiState.value.selectedBody
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
                        errorMessage = "저장 실패: ${e.message}"
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