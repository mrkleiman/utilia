package com.mariomora.utilia.domain.usecase.utilia

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.ContractAnalysisResult
import com.mariomora.utilia.domain.model.FiniquitoInput
import com.mariomora.utilia.domain.model.FiniquitoResult
import com.mariomora.utilia.domain.model.OfficialDocumentDraft
import com.mariomora.utilia.domain.repository.UtilIaRepository

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class CalculateFiniquitoUseCase(private val repository: UtilIaRepository) {
    suspend operator fun invoke(input: FiniquitoInput): AppResult<FiniquitoResult> =
        repository.calculateLaborLiquidation(input)
}

class AnalyzeContractUseCase(private val repository: UtilIaRepository) {
    suspend operator fun invoke(contractText: String): AppResult<ContractAnalysisResult> =
        repository.analyzeContract(contractText)
}

class DraftOfficialDocumentUseCase(private val repository: UtilIaRepository) {
    suspend operator fun invoke(
        title: String,
        recipient: String,
        subject: String,
        details: String
    ): AppResult<OfficialDocumentDraft> = repository.generateOfficialLetter(title, recipient, subject, details)
}
