package com.example.emotionalapp.ui.mind

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AutoTrapReportViewModel @Inject constructor(
    private val repository: AutoTrapReportRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(AutoTrapReportUiState())
    val uiState: LiveData<AutoTrapReportUiState> = _uiState

    fun loadReport() {
        _uiState.value = AutoTrapReportUiState(isLoading = true)

        repository.loadTrapStatistics(
            onSuccess = { data ->
                _uiState.value = AutoTrapReportUiState(
                    isLoading = false,
                    reportData = data,
                    errorMessage = null
                )
            },
            onFailure = { message ->
                _uiState.value = AutoTrapReportUiState(
                    isLoading = false,
                    reportData = null,
                    errorMessage = message
                )
            }
        )
    }
}