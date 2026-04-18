package com.example.emotionalapp.ui.mind.art

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionalapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ArtViewModel @Inject constructor(
    private val repository: ArtRepository
) : ViewModel() {

    private val imageResIds = listOf(
        R.drawable.art1, R.drawable.art2, R.drawable.art3,
        R.drawable.art4, R.drawable.art5, R.drawable.art6,
        R.drawable.art7, R.drawable.art8, R.drawable.art9,
        R.drawable.art10, R.drawable.art11, R.drawable.art12
    )

    private val _uiState = MutableStateFlow(ArtUiState())
    val uiState: StateFlow<ArtUiState> = _uiState.asStateFlow()

    fun goPrevPage() {
        val current = _uiState.value.currentPage
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentPage = current - 1)
        }
    }

    fun toggleImageSelection(index: Int): Boolean {
        val currentSelected = _uiState.value.selectedImages.toMutableList()

        if (currentSelected.contains(index)) {
            currentSelected.remove(index)
        } else {
            if (currentSelected.size >= 2) {
                return false
            }
            currentSelected.add(index)
        }

        _uiState.value = _uiState.value.copy(selectedImages = currentSelected)
        return true
    }

    fun updateAnswer(imageIndex: Int, answerIndex: Int, text: String) {
        val currentAnswers = _uiState.value.userAnswers.map { it.toMutableList() }.toMutableList()

        if (imageIndex in currentAnswers.indices && answerIndex in currentAnswers[imageIndex].indices) {
            currentAnswers[imageIndex][answerIndex] = text
            _uiState.value = _uiState.value.copy(userAnswers = currentAnswers)
        }
    }

    fun getImageResIdForSelected(index: Int): Int? {
        return _uiState.value.selectedImageResourceIds.getOrNull(index)
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value
        val currentPage = state.currentPage

        if (currentPage == 2) {
            if (state.selectedImages.size < 2) {
                return "이미지를 2개 선택해야 합니다."
            }
        }

        if (currentPage in 3..8) {
            val imageIndex = if (currentPage in 3..5) 0 else 1
            val pageIndex = (currentPage - 3) % 3
            val startIndex = pageIndex * 2
            val savedAnswers = state.userAnswers[imageIndex]

            val isSingleAnswerPage = pageIndex == 2
            val answer1 = savedAnswers.getOrNull(startIndex).orEmpty()
            val answer2 = savedAnswers.getOrNull(startIndex + 1).orEmpty()

            val isValid = if (isSingleAnswerPage) {
                answer1.isNotBlank()
            } else {
                answer1.isNotBlank() && answer2.isNotBlank()
            }

            if (!isValid) {
                return "모든 질문에 답변해주세요."
            }
        }

        return null
    }

    fun prepareSelectedImages() {
        val selectedIndices = _uiState.value.selectedImages.toList()
        val selectedResourceIds = selectedIndices.mapNotNull { index ->
            imageResIds.getOrNull(index)
        }

        _uiState.value = _uiState.value.copy(
            selectedImageIndices = selectedIndices,
            selectedImageResourceIds = selectedResourceIds
        )
    }

    fun goNextPage(): Boolean {
        val current = _uiState.value.currentPage
        if (current < _uiState.value.totalPages - 1) {
            _uiState.value = _uiState.value.copy(currentPage = current + 1)
            return true
        }
        return false
    }

    fun isLastPage(): Boolean {
        return _uiState.value.currentPage == _uiState.value.totalPages - 1
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun saveTraining() {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveArtTraining(
                selectedImageResourceIds = _uiState.value.selectedImageResourceIds,
                userAnswers = _uiState.value.userAnswers
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
                        errorMessage = e.message ?: "저장에 실패했습니다."
                    )
                }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}