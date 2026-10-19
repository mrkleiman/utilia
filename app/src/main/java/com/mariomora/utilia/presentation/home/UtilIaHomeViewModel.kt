package com.mariomora.utilia.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.ContractAnalysisResult
import com.mariomora.utilia.domain.model.FiniquitoInput
import com.mariomora.utilia.domain.model.FiniquitoResult
import com.mariomora.utilia.domain.model.OfficialDocumentDraft
import com.mariomora.utilia.domain.model.User
import com.mariomora.utilia.domain.usecase.auth.GetCurrentUserUseCase
import com.mariomora.utilia.domain.usecase.auth.LogoutUseCase
import com.mariomora.utilia.domain.usecase.utilia.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */

data class UtilIaUiState(
    val currentUser: User? = null,
    val selectedTab: Int = 0, // 0: Calculadora LFT, 1: Explicador Contratos, 2: Redactor de Oficios
    val finiquitoResult: FiniquitoResult? = null,
    val contractAnalysis: ContractAnalysisResult? = null,
    val officialDraft: OfficialDocumentDraft? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val copiedMessage: String? = null
)

class UtilIaHomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val calculateFiniquitoUseCase: CalculateFiniquitoUseCase,
    private val analyzeContractUseCase: AnalyzeContractUseCase,
    private val draftOfficialDocumentUseCase: DraftOfficialDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UtilIaUiState(currentUser = getCurrentUserUseCase()))
    val uiState = _uiState.asStateFlow()

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index, errorMessage = null) }
    }

    fun calculateFiniquito(
        dailySalary: Double,
        daysWorkedYear: Int,
        seniorityYears: Double,
        isDismissal: Boolean,
        pendingVacationDays: Int
    ) {
        if (dailySalary <= 0.0) {
            _uiState.update { it.copy(errorMessage = "El salario diario debe ser mayor a 0.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val input = FiniquitoInput(
                dailySalary = dailySalary,
                daysWorkedCurrentYear = daysWorkedYear,
                seniorityYears = seniorityYears,
                isUnjustifiedDismissal = isDismissal,
                pendingVacationDays = pendingVacationDays
            )
            val result = calculateFiniquitoUseCase(input)
            when (result) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, finiquitoResult = result.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                AppResult.Loading -> {}
            }
        }
    }

    fun analyzeContract(contractText: String) {
        if (contractText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Pega o escribe las cláusulas del contrato.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = analyzeContractUseCase(contractText)
            when (result) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, contractAnalysis = result.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                AppResult.Loading -> {}
            }
        }
    }

    fun draftDocument(title: String, recipient: String, subject: String, details: String) {
        if (recipient.isBlank() || details.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa el destinatario y los detalles de la petición.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = draftOfficialDocumentUseCase(title, recipient, subject, details)
            when (result) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, officialDraft = result.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                AppResult.Loading -> {}
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(errorMessage = null, copiedMessage = null) }
    }
}
