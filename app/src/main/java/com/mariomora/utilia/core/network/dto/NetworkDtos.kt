package com.mariomora.utilia.core.network.dto

import kotlinx.serialization.Serializable

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class LoginRequestDto(val email: String, val passwordHash: String)

@Serializable
data class RegisterRequestDto(val email: String, val passwordHash: String, val displayName: String)

@Serializable
data class GoogleAuthRequestDto(val googleToken: String, val email: String, val displayName: String, val photoUrl: String? = null)

@Serializable
data class AuthResponseDto(val userId: String, val email: String, val displayName: String, val token: String, val photoUrl: String? = null)

@Serializable
data class FiniquitoRequestDto(
    val dailySalary: Double,
    val daysWorkedCurrentYear: Int,
    val seniorityYears: Double,
    val isUnjustifiedDismissal: Boolean,
    val pendingVacationDays: Int
)

@Serializable
data class FiniquitoResponseDto(
    val aguinaldoProporcional: Double,
    val vacacionesProporcionales: Double,
    val primaVacacional: Double,
    val indemnizacion90Dias: Double,
    val primaAntiguedad: Double,
    val subtotalFiniquito: Double,
    val totalLiquidacion: Double,
    val isrRetencionEstimada: Double,
    val netoPagar: Double,
    val legalBasis: String
)

@Serializable
data class ContractAnalysisRequestDto(val contractText: String)

@Serializable
data class ContractAnalysisResponseDto(
    val summaryInPlainSpanish: String,
    val redFlags: List<String>,
    val workerRightsObserved: List<String>,
    val recommendation: String
)

@Serializable
data class OfficialDocumentRequestDto(
    val title: String,
    val recipient: String,
    val subject: String,
    val details: String
)

@Serializable
data class OfficialDocumentResponseDto(
    val title: String,
    val recipient: String,
    val subject: String,
    val legalArticlesReferenced: List<String>,
    val bodyText: String,
    val generatedDate: String
)
