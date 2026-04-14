package com.example.emotionalapp.ui.mind.auto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AutoReportViewModel(
    private val repository: AutoReportRepository = AutoReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(AutoReportUiState())
    val uiState: LiveData<AutoReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = AutoReportUiState(isLoading = true)

        repository.loadAutoReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = AutoReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null
                )
            },
            onFailure = { message ->
                _uiState.value = AutoReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message
                )
            }
        )
    }
}