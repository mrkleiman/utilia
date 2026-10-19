package com.mariomora.utilia.domain

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.core.common.DispatcherProvider
import com.mariomora.utilia.core.network.ApiEndpoints
import com.mariomora.utilia.core.network.dto.*
import com.mariomora.utilia.data.repository.UtilIaRepositoryImpl
import com.mariomora.utilia.domain.model.FiniquitoInput
import com.mariomora.utilia.domain.usecase.utilia.AnalyzeContractUseCase
import com.mariomora.utilia.domain.usecase.utilia.CalculateFiniquitoUseCase
import com.mariomora.utilia.domain.usecase.utilia.DraftOfficialDocumentUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class UtilIaLaborCalculationTest {
    private lateinit var repo: UtilIaRepositoryImpl

    @Before
    fun setup() {
        val fakeApi = object : ApiEndpoints {
            override suspend fun loginWithEmail(request: LoginRequestDto) = ApiResponse(true, "OK", AuthResponseDto("1", request.email, "Ciudadano", "tok"))
            override suspend fun register(request: RegisterRequestDto) = ApiResponse(true, "OK", AuthResponseDto("1", request.email, request.displayName, "tok"))
            override suspend fun loginWithGoogle(request: GoogleAuthRequestDto) = ApiResponse(true, "OK", AuthResponseDto("1", request.email, request.displayName, "tok"))
            override suspend fun calculateFiniquito(request: FiniquitoRequestDto) = ApiResponse(true, "OK", FiniquitoResponseDto(3750.0, 2400.0, 600.0, 45000.0, 7560.96, 6750.0, 59310.96, 4820.0, 54490.96, "LFT"))
            override suspend fun analyzeContract(request: ContractAnalysisRequestDto) = ApiResponse(true, "OK", ContractAnalysisResponseDto("Contrato revisado", listOf("Alerta"), listOf("Derecho"), "Recomendación"))
            override suspend fun draftOfficialLetter(request: OfficialDocumentRequestDto) = ApiResponse(true, "OK", OfficialDocumentResponseDto("Oficio", "RH", "Asunto", listOf("Art. 8°"), "Cuerpo", "Hoy"))
        }
        val disp = object : DispatcherProvider {
            override val main: CoroutineDispatcher = Dispatchers.Unconfined
            override val io: CoroutineDispatcher = Dispatchers.Unconfined
            override val default: CoroutineDispatcher = Dispatchers.Unconfined
            override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
        }
        repo = UtilIaRepositoryImpl(fakeApi, disp)
    }

    @Test
    fun testFiniquitoJustifiedDismissalOrResignation() = runBlocking {
        // Voluntario: Solo prestaciones devengadas irrenunciables, sin 90 días
        val input = FiniquitoInput(
            dailySalary = 500.0,
            daysWorkedCurrentYear = 365,
            seniorityYears = 2.0,
            isUnjustifiedDismissal = false,
            pendingVacationDays = 0
        )
        val useCase = CalculateFiniquitoUseCase(repo)
        val result = useCase(input)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(7500.0, data.aguinaldoProporcional, 0.01) // 15 días * $500
        assertEquals(7000.0, data.vacacionesProporcionales, 0.01) // Año 2: 14 días * $500
        assertEquals(1750.0, data.primaVacacional, 0.01) // 25% de $7,000
        assertEquals(0.0, data.indemnizacion90Dias, 0.01) // No indemnización
        assertEquals(data.subtotalFiniquito, data.totalLiquidacion, 0.01)
    }

    @Test
    fun testFiniquitoUnjustifiedDismissalIncludes90Days() = runBlocking {
        // Despido injustificado: Incluye 90 días de salario (Art. 48 LFT)
        val input = FiniquitoInput(
            dailySalary = 400.0,
            daysWorkedCurrentYear = 182,
            seniorityYears = 3.0,
            isUnjustifiedDismissal = true,
            pendingVacationDays = 2
        )
        val useCase = CalculateFiniquitoUseCase(repo)
        val result = useCase(input)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(36000.0, data.indemnizacion90Dias, 0.01) // 90 * $400
        assertTrue("Prima de antigüedad debe aplicarse en despido injustificado", data.primaAntiguedad > 0)
        assertTrue(data.totalLiquidacion > data.subtotalFiniquito)
    }

    @Test
    fun testAnalyzeContractSubordinationFlag() = runBlocking {
        val useCase = AnalyzeContractUseCase(repo)
        val contractText = "El prestador tendrá horario obligatorio de 9am a 6pm bajo órdenes de su jefe de área."
        val result = useCase(contractText)

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertNotNull(data.summaryInPlainSpanish)
        assertTrue(data.redFlags.isNotEmpty())
    }

    @Test
    fun testDraftOfficialDocumentArticle8() = runBlocking {
        val useCase = DraftOfficialDocumentUseCase(repo)
        val result = useCase("Petición de Finiquito", "Recursos Humanos S.A.", "Pago de liquidación", "Despido sin causa comprobada")

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertTrue(data.bodyText.contains("Artículo 8°"))
        assertTrue(data.legalArticlesReferenced.contains("Art. 8° Constitucional"))
    }
}
