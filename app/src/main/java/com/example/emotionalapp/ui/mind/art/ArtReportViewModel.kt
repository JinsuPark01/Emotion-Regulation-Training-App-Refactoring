package com.example.emotionalapp.ui.mind.art

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArtReportViewModel(
    private val repository: ArtReportRepository = ArtReportRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData(ArtReportUiState())
    val uiState: LiveData<ArtReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long) {
        _uiState.value = ArtReportUiState(isLoading = true)

        repository.loadArtReport(
            reportDateMillis = reportDateMillis,
            onSuccess = { data ->
                _uiState.value = ArtReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null
                )
            },
            onFailure = { message ->
                _uiState.value = ArtReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message
                )
            }
        )
    }
}