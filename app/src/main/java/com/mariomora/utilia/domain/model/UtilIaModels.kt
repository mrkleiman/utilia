package com.mariomora.utilia.domain.model

import kotlinx.serialization.Serializable

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 *
 * Domain Entity: UtilIA models for Mexican labor, legal, and formal document automation.
 */

@Serializable
data class FiniquitoInput(
    val dailySalary: Double,
    val daysWorkedCurrentYear: Int,
    val seniorityYears: Double,
    val isUnjustifiedDismissal: Boolean, // Despido injustificado (aplica 90 días + prima de antigüedad)
    val pendingVacationDays: Int = 0
)

@Serializable
data class FiniquitoResult(
    val aguinaldoProporcional: Double,
    val vacacionesProporcionales: Double,
    val primaVacacional: Double, // 25% mínimo legal LFT Art. 80
    val indemnizacion90Dias: Double, // Art. 48 LFT
    val primaAntiguedad: Double, // Art. 162 LFT (12 días por año con tope de 2 salarios mínimos)
    val subtotalFiniquito: Double,
    val totalLiquidacion: Double,
    val isrRetencionEstimada: Double,
    val netoPagar: Double,
    val legalBasis: String
)

@Serializable
data class ContractAnalysisResult(
    val summaryInPlainSpanish: String,
    val redFlags: List<String>,
    val workerRightsObserved: List<String>,
    val recommendation: String
)

@Serializable
data class OfficialDocumentDraft(
    val title: String,
    val recipient: String,
    val subject: String,
    val legalArticlesReferenced: List<String>,
    val bodyText: String,
    val generatedDate: String
)
