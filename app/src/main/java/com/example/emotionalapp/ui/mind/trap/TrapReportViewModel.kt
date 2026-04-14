package com.example.emotionalapp.ui.mind.trap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrapReportViewModel(
    private val repository: TrapReportRepository = TrapReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(TrapReportUiState())
    val uiState: LiveData<TrapReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = TrapReportUiState(isLoading = true)

        repository.loadTrapReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = TrapReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = TrapReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = TrapReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}