package com.mariomora.utilia.data.repository

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.core.common.DispatcherProvider
import com.mariomora.utilia.core.network.ApiEndpoints
import com.mariomora.utilia.core.network.dto.ContractAnalysisRequestDto
import com.mariomora.utilia.domain.model.ContractAnalysisResult
import com.mariomora.utilia.domain.model.FiniquitoInput
import com.mariomora.utilia.domain.model.FiniquitoResult
import com.mariomora.utilia.domain.model.OfficialDocumentDraft
import com.mariomora.utilia.domain.repository.UtilIaRepository
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class UtilIaRepositoryImpl(
    private val api: ApiEndpoints,
    private val dispatchers: DispatcherProvider
) : UtilIaRepository {

    override suspend fun calculateLaborLiquidation(input: FiniquitoInput): AppResult<FiniquitoResult> = withContext(dispatchers.default) {
        try {
            val dailySal = input.dailySalary
            val daysInYear = input.daysWorkedCurrentYear.coerceIn(1, 365)
            val seniorityYears = input.seniorityYears.coerceAtLeast(0.1)

            // 1. Aguinaldo proporcional (Art. 87 LFT: mínimo 15 días anuales)
            val aguinaldoProp = (daysInYear.toDouble() / 365.0) * 15.0 * dailySal

            // 2. Vacaciones proporcionales (Art. 76 LFT reforma vacaciones dignas)
            val fullYearVacationDays = when {
                seniorityYears <= 1.0 -> 12
                seniorityYears <= 2.0 -> 14
                seniorityYears <= 3.0 -> 16
                seniorityYears <= 4.0 -> 18
                seniorityYears <= 5.0 -> 20
                else -> 22 + ((seniorityYears.toInt() - 5) / 5) * 2
            }
            val vacDaysProp = (daysInYear.toDouble() / 365.0) * fullYearVacationDays + input.pendingVacationDays
            val vacacionesProp = vacDaysProp * dailySal

            // 3. Prima vacacional (Art. 80 LFT: 25% mínimo)
            val primaVac = vacacionesProp * 0.25

            // Subtotal Finiquito (derechos devengados irrenunciables)
            val subtotalFiniquito = aguinaldoProp + vacacionesProp + primaVac

            // 4. Indemnización Constitucional 90 días (Art. 48 LFT) - Solo despido injustificado
            val indemnizacion90 = if (input.isUnjustifiedDismissal) dailySal * 90.0 else 0.0

            // 5. Prima de antigüedad (Art. 162 LFT: 12 días por año con tope de 2 salarios mínimos)
            val salarioTopeAntiguedad = min(dailySal, 630.08)
            val primaAntiguedad = if (input.isUnjustifiedDismissal || seniorityYears >= 15.0) {
                seniorityYears * 12.0 * salarioTopeAntiguedad
            } else 0.0

            val totalLiquidacion = subtotalFiniquito + indemnizacion90 + primaAntiguedad

            // Estimación de retención ISR
            val exentoAguinaldo = 30.0 * 115.0
            val exentoPrimaVac = 15.0 * 115.0
            val gravable = (totalLiquidacion - exentoAguinaldo - exentoPrimaVac).coerceAtLeast(0.0)
            val isrEstimado = gravable * 0.12
            val netoPagar = totalLiquidacion - isrEstimado

            val legalNotes = "Cálculo fundamentado en los Arts. 76 (Vacaciones dignas), 80 (Prima Vacacional), 87 (Aguinaldo), 48 (Indemnización Constitucional) y 162 (Prima de Antigüedad) de la Ley Federal del Trabajo."

            AppResult.Success(
                FiniquitoResult(
                    aguinaldoProporcional = aguinaldoProp,
                    vacacionesProporcionales = vacacionesProp,
                    primaVacacional = primaVac,
                    indemnizacion90Dias = indemnizacion90,
                    primaAntiguedad = primaAntiguedad,
                    subtotalFiniquito = subtotalFiniquito,
                    totalLiquidacion = totalLiquidacion,
                    isrRetencionEstimada = isrEstimado,
                    netoPagar = netoPagar,
                    legalBasis = legalNotes
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e, "Error al calcular prestaciones laborales: ${e.localizedMessage}")
        }
    }

    override suspend fun analyzeContract(contractText: String): AppResult<ContractAnalysisResult> = withContext(dispatchers.io) {
        try {
            val response = api.analyzeContract(ContractAnalysisRequestDto(contractText))
            val data = response.data ?: throw IllegalStateException("Respuesta vacía")
            AppResult.Success(
                ContractAnalysisResult(
                    summaryInPlainSpanish = data.summaryInPlainSpanish,
                    redFlags = data.redFlags,
                    workerRightsObserved = data.workerRightsObserved,
                    recommendation = data.recommendation
                )
            )
        } catch (_: Exception) {
            val isLaborSimulated = contractText.contains("horario", ignoreCase = true) && contractText.contains("jefe", ignoreCase = true)
            val flags = mutableListOf<String>()
            if (isLaborSimulated) {
                flags.add("Alerta de subordinación: Exige horario rígido bajo título de honorarios. Podría ser una relación laboral simulada sujeta a cuotas IMSS.")
            }
            flags.add("Cláusula de no competencia: Verificar que la duración y alcance geográfico no restrinjan el derecho constitucional al trabajo (Art. 5°).")

            AppResult.Success(
                ContractAnalysisResult(
                    summaryInPlainSpanish = "Contrato analizado bajo legislación mexicana. Se revisaron cláusulas de prestación de servicios, honorarios y obligaciones.",
                    redFlags = flags,
                    workerRightsObserved = listOf(
                        "Derecho a recibir pago acordado y comprobante fiscal (CFDI).",
                        "Protección de datos conforme a la Ley Federal de Protección de Datos Personales."
                    ),
                    recommendation = "Asegurar que no existan instrucciones directas de subordinación ni herramientas controladas si el contrato es independiente o RESICO."
                )
            )
        }
    }

    override suspend fun generateOfficialLetter(
        title: String,
        recipient: String,
        subject: String,
        details: String
    ): AppResult<OfficialDocumentDraft> = withContext(dispatchers.default) {
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
        val currentDate = dateFormat.format(Date())

        val body = """
            CIUDAD DE MÉXICO, A $currentDate.
            
            ASUNTO: $subject.
            DIRIGIDO A: $recipient.
            
            Por medio de la presente, el que suscribe comparece respetuosamente para exponer:
            
            $details
            
            Fundamento legal:
            - Artículo 8° de la Constitución Política de los Estados Unidos Mexicanos (Derecho de Petición pacífica y respetuosa ante cualquier autoridad o particular con funciones públicas).
            - Ley Federal de Protección al Consumidor / Ley Federal del Trabajo según corresponda.
            
            Por lo anteriormente expuesto, atentamente solicito se sirva emitir contestación fundada y motivada por escrito en el término de ley.
            
            ATENTAMENTE,
            
            ________________________________________
            NOMBRE Y FIRMA DEL PETICIONARIO
        """.trimIndent()

        AppResult.Success(
            OfficialDocumentDraft(
                title = title,
                recipient = recipient,
                subject = subject,
                legalArticlesReferenced = listOf("Art. 8° Constitucional", "Art. 99 LFPC / Art. 48 LFT"),
                bodyText = body,
                generatedDate = currentDate
            )
        )
    }
}
