package com.example.emotionalapp.ui.mind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AutoViewModel(
    private val repository: AutoRepository = AutoRepository()
) : ViewModel() {

    val trapOptions = listOf(
        "성급하게 결론짓기\n -이 비행기가 추락할 확률은 90%야. (실제 확률은 0.000013%)",
        "최악을 생각하기\n -부모님이 집에 늦게 들어오시네. 사고를 당한 것 같아.",
        "긍정적인 면 무시하기\n -시험문제가 우연히 쉬워서 좋은 점수를 받았을 뿐이야.",
        "흑백사고\n -시험에서 100점을 받지 못한다면 나는 실패자야.",
        "점쟁이 사고 (지레짐작하기)\n -연주회를 망칠 거야, 공연을 하지 않겠어.",
        "독심술\n -한 번도 대화를 나누지는 않았지만, 쟤는 나를 좋아하지 않아.",
        "정서적 추리\n -애인이 일 때문에 늦는다고 했지만, 그게 아닌 것 같아. 직감이 와. 나를 속이는 게 틀림없어.",
        "꼬리표 붙이기\n -나는 멍청해.",
        "“해야만 한다“는 진술문\n -사람들은 모두 정직해야해. 거짓말을 하는 건 있을 수 없는 일이야.",
        "마술적 사고\n -내가 아버지에게 전화를 걸면 아버지는 사고를 피할 수 있을 거야."
    )

    private val _uiState = MutableStateFlow(AutoUiState())
    val uiState: StateFlow<AutoUiState> = _uiState.asStateFlow()

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

    fun updateAnswer(index: Int, text: String) {
        val currentAnswers = _uiState.value.answerList.toMutableList()
        if (index in currentAnswers.indices) {
            currentAnswers[index] = text
            _uiState.value = _uiState.value.copy(answerList = currentAnswers)
        }
    }

    fun selectTrap(index: Int) {
        _uiState.value = _uiState.value.copy(
            selectedTrapIndex = index,
            selectedTrapText = trapOptions.getOrElse(index) { "" }
        )
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value

        return when (state.currentPage) {
            1 -> {
                if (
                    state.answerList[0].isBlank() ||
                    state.answerList[1].isBlank() ||
                    state.answerList[2].isBlank()
                ) {
                    "모든 항목을 입력해주세요."
                } else null
            }

            2 -> {
                if (state.selectedTrapIndex == -1) {
                    "생각의 덫 항목을 선택해주세요."
                } else null
            }

            3 -> {
                if (state.answerList[4].isBlank()) {
                    "답변을 입력해주세요."
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

            val result = repository.saveAutoTraining(_uiState.value)

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