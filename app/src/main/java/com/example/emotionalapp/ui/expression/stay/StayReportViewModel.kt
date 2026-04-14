package com.example.emotionalapp.ui.expression.stay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StayReportViewModel(
    private val repository: StayReportRepository = StayReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(StayReportUiState())
    val uiState: LiveData<StayReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = StayReportUiState(isLoading = true)

        repository.loadStayReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = StayReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = StayReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = StayReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}