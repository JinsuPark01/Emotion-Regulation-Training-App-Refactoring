package com.example.emotionalapp.ui.expression.stay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class StayViewModel @Inject constructor(
    private val repository: StayRepository
) : ViewModel() {

    val emotions = listOf(
        "행복", "즐거움", "자신감",
        "슬픔", "두려움", "당황",
        "걱정", "짜증", "분노"
    )

    private val _uiState = MutableStateFlow(StayUiState())
    val uiState: StateFlow<StayUiState> = _uiState.asStateFlow()

    fun loadInitialState() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.isFirstTraining()

            result
                .onSuccess { isFirst ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isFirstTraining = isFirst,
                        selectedTimerMillis = if (isFirst) 60_000L else _uiState.value.selectedTimerMillis
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "초기 데이터를 불러오지 못했습니다."
                    )
                }
        }
    }

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

    fun selectEmotion(emotion: String) {
        _uiState.value = _uiState.value.copy(selectedEmotion = emotion)
    }

    fun selectTimerMillis(millis: Long) {
        _uiState.value = _uiState.value.copy(selectedTimerMillis = millis)
    }

    fun updateClarifiedEmotion(text: String) {
        _uiState.value = _uiState.value.copy(clarifiedEmotion = text)
    }

    fun updateMoodChanged(text: String) {
        _uiState.value = _uiState.value.copy(moodChanged = text)
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value

        return when (state.currentPage) {
            0 -> {
                if (state.selectedEmotion == null) {
                    "오늘의 감정을 선택해주세요."
                } else null
            }

            2 -> {
                if (state.clarifiedEmotion.isBlank() || state.moodChanged.isBlank()) {
                    "모든 항목을 입력해주세요."
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