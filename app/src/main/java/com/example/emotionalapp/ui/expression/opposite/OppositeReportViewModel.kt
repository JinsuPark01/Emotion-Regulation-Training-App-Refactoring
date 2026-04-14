package com.example.emotionalapp.ui.expression.opposite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OppositeReportViewModel(
    private val repository: OppositeReportRepository = OppositeReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(OppositeReportUiState())
    val uiState: LiveData<OppositeReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = OppositeReportUiState(isLoading = true)

        repository.loadOppositeReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = OppositeReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = OppositeReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = OppositeReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}