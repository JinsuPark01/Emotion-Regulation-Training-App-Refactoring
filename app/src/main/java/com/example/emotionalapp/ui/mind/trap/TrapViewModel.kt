package com.example.emotionalapp.ui.mind.trap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrapViewModel @Inject constructor(
    private val repository: TrapRepository
) : ViewModel() {

    private val page2Options = listOf(
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

    val page3Options = listOf(
        "그 생각이 확실할까요?\n - 생각의 타당성 점검하기",
        "그 생각이 만약 실제라면 얼마나 나쁠까요?\n - 생각을 실제로 가정하기",
        "객관적으로 살펴볼까요?\n - 관점을 다르게 해보기"
    )

    private val _uiState = MutableStateFlow(TrapUiState())
    val uiState: StateFlow<TrapUiState> = _uiState.asStateFlow()

    fun goPrevPage() {
        val current = _uiState.value.currentPage
        if (current > 0 && current != 5) {
            _uiState.value = _uiState.value.copy(currentPage = current - 1)
        }
    }

    fun goToPage(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
    }

    fun moveToNextPageOrFinish(): Boolean {
        val current = _uiState.value.currentPage
        return if (current < _uiState.value.totalPages - 1) {
            _uiState.value = _uiState.value.copy(currentPage = current + 1)
            false
        } else {
            true
        }
    }

    fun updatePage1Answers(answer1: String, answer2: String) {
        _uiState.value = _uiState.value.copy(
            responsePage1Answer1 = answer1,
            responsePage1Answer2 = answer2
        )
    }

    fun selectPage2Option(index: Int) {
        _uiState.value = _uiState.value.copy(
            selectedPage2Index = index,
            responsePage2Text = page2Options.getOrElse(index) { "" }
        )
    }

    fun selectTrapOption(index: Int) {
        _uiState.value = _uiState.value.copy(
            selectedTrapIndex = index
        )
    }

    fun updatePage4Answer(answerIndex: Int, text: String) {
        val state = _uiState.value
        when (state.selectedTrapIndex) {
            0 -> {
                val updated = state.responsePage4ZeroAnswers.toMutableList()
                if (answerIndex in updated.indices) {
                    updated[answerIndex] = text
                    _uiState.value = state.copy(responsePage4ZeroAnswers = updated)
                }
            }
            1 -> {
                val updated = state.responsePage4OneAnswers.toMutableList()
                if (answerIndex in updated.indices) {
                    updated[answerIndex] = text
                    _uiState.value = state.copy(responsePage4OneAnswers = updated)
                }
            }
            2 -> {
                val updated = state.responsePage4TwoAnswers.toMutableList()
                if (answerIndex in updated.indices) {
                    updated[answerIndex] = text
                    _uiState.value = state.copy(responsePage4TwoAnswers = updated)
                }
            }
        }
    }

    fun updatePage6Answer(text: String) {
        _uiState.value = _uiState.value.copy(
            responsePage6Text = text
        )
    }

    fun validateCurrentPage(): String? {
        val state = _uiState.value

        return when (state.currentPage) {
            1 -> {
                if (state.responsePage1Answer1.isBlank() || state.responsePage1Answer2.isBlank()) {
                    "모든 질문에 답변해주세요."
                } else null
            }

            2 -> {
                if (state.selectedPage2Index !in page2Options.indices) {
                    "덫을 선택해주세요."
                } else null
            }

            3 -> {
                if (state.selectedTrapIndex == -1) {
                    "질문을 선택해주세요."
                } else null
            }

            4 -> {
                when (state.selectedTrapIndex) {
                    0 -> {
                        if (state.responsePage4ZeroAnswers.any { it.isBlank() }) "모든 항목을 입력해주세요." else null
                    }
                    1 -> {
                        if (state.responsePage4OneAnswers.any { it.isBlank() }) "모든 항목을 입력해주세요." else null
                    }
                    2 -> {
                        if (state.responsePage4TwoAnswers.any { it.isBlank() }) "모든 항목을 입력해주세요." else null
                    }
                    else -> "질문을 선택해주세요."
                }
            }

            6 -> {
                if (state.responsePage6Text.isBlank()) "모든 항목을 입력해주세요." else null
            }

            else -> null
        }
    }

    fun shouldSaveAtCurrentPage(): Boolean {
        return _uiState.value.currentPage == _uiState.value.totalPages - 1
    }

    fun saveTraining() {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            val result = repository.saveMindTrapTraining(_uiState.value)

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