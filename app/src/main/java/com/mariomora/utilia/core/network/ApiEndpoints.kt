package com.mariomora.utilia.core.network

import com.mariomora.utilia.core.network.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
interface ApiEndpoints {
    @POST("api/auth/login")
    suspend fun loginWithEmail(@Body request: LoginRequestDto): ApiResponse<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ApiResponse<AuthResponseDto>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequestDto): ApiResponse<AuthResponseDto>

    @POST("api/labor/finiquito")
    suspend fun calculateFiniquito(@Body request: FiniquitoRequestDto): ApiResponse<FiniquitoResponseDto>

    @POST("api/legal/analyze-contract")
    suspend fun analyzeContract(@Body request: ContractAnalysisRequestDto): ApiResponse<ContractAnalysisResponseDto>

    @POST("api/legal/draft-official")
    suspend fun draftOfficialLetter(@Body request: OfficialDocumentRequestDto): ApiResponse<OfficialDocumentResponseDto>
}
