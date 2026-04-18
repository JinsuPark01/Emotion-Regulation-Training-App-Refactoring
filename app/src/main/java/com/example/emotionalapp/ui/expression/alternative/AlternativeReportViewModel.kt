package com.example.emotionalapp.ui.expression.alternative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AlternativeReportViewModel @Inject constructor(
    private val repository: AlternativeReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(AlternativeReportUiState())
    val uiState: LiveData<AlternativeReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = AlternativeReportUiState(isLoading = true)

        repository.loadAlternativeReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = AlternativeReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = AlternativeReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = AlternativeReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}