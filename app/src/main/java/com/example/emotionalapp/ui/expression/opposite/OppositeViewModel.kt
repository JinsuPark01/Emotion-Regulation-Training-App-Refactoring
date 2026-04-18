package com.example.emotionalapp.ui.expression.opposite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OppositeViewModel @Inject constructor(
    private val repository: OppositeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OppositeUiState())
    val uiState: StateFlow<OppositeUiState> = _uiState.asStateFlow()

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

    fun updateAnswer1(text: String) {
        _uiState.value = _uiState.value.copy(answer1 = text)
    }

    fun updateAnswer2(text: String) {
        _uiState.value = _uiState.value.copy(answer2 = text)
    }

    fun updateAnswer3(text: String) {
        _uiState.value = _uiState.value.copy(answer3 = text)
    }

    fun updateAnswer5(text: String) {
        _uiState.value = _uiState.value.copy(answer5 = text)
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value
        return if (state.currentPage == 1) {
            if (
                state.answer1.isBlank() ||
                state.answer2.isBlank() ||
                state.answer3.isBlank() ||
                state.answer5.isBlank()
            ) {
                "모든 항목을 입력해주세요."
            } else null
        } else {
            null
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