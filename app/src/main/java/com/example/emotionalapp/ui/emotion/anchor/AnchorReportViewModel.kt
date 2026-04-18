package com.example.emotionalapp.ui.emotion.anchor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AnchorReportViewModel @Inject constructor(
    private val repository: AnchorReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(AnchorReportUiState())
    val uiState: LiveData<AnchorReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = AnchorReportUiState(isLoading = true)

        repository.loadAnchorReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = AnchorReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null,
                    shouldNavigateToLogin = false
                )
            },
            onFailure = { message ->
                _uiState.value = AnchorReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message,
                    shouldNavigateToLogin = false
                )
            },
            onRequireLogin = {
                _uiState.value = AnchorReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = null,
                    shouldNavigateToLogin = true
                )
            }
        )
    }
}