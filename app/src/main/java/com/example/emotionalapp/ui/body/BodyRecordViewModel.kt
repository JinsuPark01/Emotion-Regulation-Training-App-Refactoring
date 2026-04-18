package com.example.emotionalapp.ui.body

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BodyRecordViewModel @Inject constructor(
    private val repository: BodyRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyRecordUiState())
    val uiState: StateFlow<BodyRecordUiState> = _uiState.asStateFlow()

    fun setTrainingId(trainingId: String) {
        _uiState.value = _uiState.value.copy(trainingId = trainingId)
    }

    fun updateFeedbackText(text: String) {
        _uiState.value = _uiState.value.copy(feedbackText = text)
    }

    fun validate(): String? {
        return if (_uiState.value.feedbackText.isBlank()) {
            "소감을 입력해주세요."
        } else {
            null
        }
    }

    fun save(context: Context) {
        if (_uiState.value.isSaving) return

        val trainingId = _uiState.value.trainingId
        if (trainingId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "훈련 정보를 확인할 수 없습니다."
            )
            return
        }

        val feedbackText = _uiState.value.feedbackText.trim()
        if (feedbackText.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "소감을 입력해주세요."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveBodyRecord(
                context = context,
                trainingId = trainingId,
                feedbackText = feedbackText
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
                        errorMessage = e.message ?: "저장 실패"
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