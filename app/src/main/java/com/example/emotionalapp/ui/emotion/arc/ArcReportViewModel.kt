package com.example.emotionalapp.ui.emotion.arc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ArcReportViewModel @Inject constructor(
    private val repository: ArcReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(ArcReportUiState())
    val uiState: LiveData<ArcReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = ArcReportUiState(isLoading = true)

        repository.loadArcReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = ArcReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = ArcReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = ArcReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}