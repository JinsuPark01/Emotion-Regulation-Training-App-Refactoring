package com.example.emotionalapp.ui.emotion.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectReportViewModel(
    private val repository: SelectReportRepository = SelectReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(SelectReportUiState())
    val uiState: LiveData<SelectReportUiState> = _uiState

    fun loadReport() {
        _uiState.value = SelectReportUiState(isLoading = true)

        repository.loadSelectReport(
            onSuccess = { data ->
                _uiState.value = SelectReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null
                )
            },
            onFailure = { message ->
                _uiState.value = SelectReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message
                )
            }
        )
    }
}