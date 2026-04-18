package com.example.emotionalapp.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class WeeklyReportViewModel @Inject constructor(
    private val repository: WeeklyReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(WeeklyReportUiState())
    val uiState: LiveData<WeeklyReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = WeeklyReportUiState(isLoading = true)

        repository.loadWeeklyReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = WeeklyReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = WeeklyReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = WeeklyReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }

    fun interpretPhq9(score: Int): String = when {
        score < 0 -> "해당 주차에 대한 정보가 없습니다."
        score <= 4 -> "정상입니다. 적응 상 어려움을 초래할만한 우울관련 증상을 거의 보고하지 않았습니다."
        score <= 9 -> "경미한 수준입니다. 약간의 우울감이 있으나 일상생활에 지장을 줄 정도는 아닙니다."
        score <= 14 -> "중간 수준의 우울감입니다. 2주 연속 지속될 경우 일상생활(직업적, 사회적)에 다소 영향을 미칠 수 있어 관심이 필요합니다."
        score <= 19 -> "약간 심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)에 영향을 미칠 경우, 정신건강전문가의 도움을 받아보세요."
        else -> "심한 수준의 우울감입니다. 2주 연속 지속되며 일상생활(직업적, 사회적)의 다양한 영역에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretGad7(score: Int): String = when {
        score < 0 -> "해당 주차에 대한 정보가 없습니다."
        score <= 4 -> "정상입니다. 주의가 필요할 정도의 불안을 보고하지 않았습니다."
        score <= 9 -> "다소 경미한 수준의 걱정과 불안을 경험하는 것으로 보입니다."
        score <= 14 -> "주의가 필요한 수준의 과도한 걱정과 불안을 보고하였습니다. 2주 연속 지속될 경우 정신건강전문가의 도움을 받아보세요."
        else -> "과도하고 심한 걱정과 불안을 보고하였습니다. 2주 연속 지속되며 일상생활에서 어려움을 겪을 경우, 추가적인 평가나 정신건강전문가의 도움을 받아보시기 바랍니다."
    }

    fun interpretPanas(pa: Int, na: Int): String = when {
        pa < 0 && na < 0 -> "해당 주차에 대한 정보가 없습니다."
        pa > na -> "긍정 감정 우세"
        pa < na -> "부정 감정 우세"
        else -> "긍·부정 감정 균형"
    }
}