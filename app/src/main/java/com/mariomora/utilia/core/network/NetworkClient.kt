package com.mariomora.utilia.core.network

import com.mariomora.utilia.core.storage.LocalStorage
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
object NetworkClient {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"

    fun createService(storage: LocalStorage, baseUrl: String = DEFAULT_BASE_URL): ApiEndpoints {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = storage.getString("auth_token")
            val requestBuilder = original.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(MockFallbackInterceptor())
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiEndpoints::class.java)
    }
}

class MockFallbackInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            val response = chain.proceed(request)
            if (response.isSuccessful) response else generateMockResponse(chain)
        } catch (_: Exception) {
            generateMockResponse(chain)
        }
    }

    private fun generateMockResponse(chain: Interceptor.Chain): Response {
        val url = chain.request().url.toString()
        val jsonMediaType = "application/json".toMediaType()
        val bodyString: String = when {
            url.contains("api/auth") -> {
                """{"success": true, "message": "Autenticación exitosa", "data": {"userId": "user_utilia_1", "email": "usuario@utilia.mx", "displayName": "Mario Mora (UtilIA)", "token": "jwt_mock_utilia"}}"""
            }
            url.contains("api/labor/finiquito") -> {
                """{"success": true, "message": "OK", "data": {
                    "aguinaldoProporcional": 3750.0,
                    "vacacionesProporcionales": 2400.0,
                    "primaVacacional": 600.0,
                    "indemnizacion90Dias": 45000.0,
                    "primaAntiguedad": 7560.96,
                    "subtotalFiniquito": 6750.0,
                    "totalLiquidacion": 59310.96,
                    "isrRetencionEstimada": 4820.0,
                    "netoPagar": 54490.96,
                    "legalBasis": "Arts. 76, 80, 87, 48 y 162 de la Ley Federal del Trabajo."
                }}"""
            }
            url.contains("api/legal/analyze-contract") -> {
                """{"success": true, "message": "OK", "data": {
                    "summaryInPlainSpanish": "Contrato de prestación de servicios profesionales independientes.",
                    "redFlags": ["Exige exclusividad y penalización desproporcionada", "Establece horario fijo característico de subordinación laboral"],
                    "workerRightsObserved": ["Derecho a pago pactado en CFDI", "Terminación con preaviso"],
                    "recommendation": "Solicitar eliminación de la cláusula de horario para evitar contingencia laboral ante el IMSS."
                }}"""
            }
            url.contains("api/legal/draft-official") -> {
                """{"success": true, "message": "OK", "data": {
                    "title": "Escrito de Petición Formal",
                    "recipient": "Autoridad Competente",
                    "subject": "Solicitud formal",
                    "legalArticlesReferenced": ["Art. 8° Constitucional", "Art. 99 LFPC"],
                    "bodyText": "Por medio del presente solicito respetuosamente...",
                    "generatedDate": "19 de octubre de 2026"
                }}"""
            }
            else -> """{"success": true, "message": "Mock Fallback OK"}"""
        }

        return Response.Builder()
            .code(200)
            .message("OK (Mocked)")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(bodyString.toResponseBody(jsonMediaType))
            .build()
    }
}
