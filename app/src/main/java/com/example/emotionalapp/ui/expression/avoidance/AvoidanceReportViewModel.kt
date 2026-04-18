package com.example.emotionalapp.ui.expression.avoidance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AvoidanceReportViewModel @Inject constructor(
    private val repository: AvoidanceReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(AvoidanceReportUiState())
    val uiState: LiveData<AvoidanceReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = AvoidanceReportUiState(isLoading = true)

        repository.loadAvoidanceReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = AvoidanceReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = AvoidanceReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = AvoidanceReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}