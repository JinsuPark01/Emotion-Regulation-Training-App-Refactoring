package com.example.emotionalapp.ui.body

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class BodyReportViewModel @Inject constructor(
    private val repository: BodyReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(BodyReportUiState())
    val uiState: LiveData<BodyReportUiState> = _uiState

    fun loadReport(reportDateMillis: Long, trainingId: String) {
        _uiState.value = BodyReportUiState(isLoading = true)

        repository.loadBodyReport(
            reportDateMillis = reportDateMillis,
            trainingIdFromIntent = trainingId,
            onSuccess = { data ->
                _uiState.value = BodyReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null
                )
            },
            onFailure = { message ->
                _uiState.value = BodyReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message
                )
            }
        )
    }
}