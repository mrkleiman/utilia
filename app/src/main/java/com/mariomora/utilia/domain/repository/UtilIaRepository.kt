package com.mariomora.utilia.domain.repository

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.ContractAnalysisResult
import com.mariomora.utilia.domain.model.FiniquitoInput
import com.mariomora.utilia.domain.model.FiniquitoResult
import com.mariomora.utilia.domain.model.OfficialDocumentDraft

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
interface UtilIaRepository {
    suspend fun calculateLaborLiquidation(input: FiniquitoInput): AppResult<FiniquitoResult>
    suspend fun analyzeContract(contractText: String): AppResult<ContractAnalysisResult>
    suspend fun generateOfficialLetter(
        title: String,
        recipient: String,
        subject: String,
        details: String
    ): AppResult<OfficialDocumentDraft>
}
