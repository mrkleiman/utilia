package com.mariomora.utilia.core.di

import android.content.Context
import com.mariomora.utilia.core.common.DefaultDispatcherProvider
import com.mariomora.utilia.core.common.DispatcherProvider
import com.mariomora.utilia.core.network.ApiEndpoints
import com.mariomora.utilia.core.network.NetworkClient
import com.mariomora.utilia.core.storage.LocalStorage
import com.mariomora.utilia.core.storage.SharedPreferencesStorage
import com.mariomora.utilia.data.repository.AuthRepositoryImpl
import com.mariomora.utilia.data.repository.UtilIaRepositoryImpl
import com.mariomora.utilia.domain.repository.AuthRepository
import com.mariomora.utilia.domain.repository.UtilIaRepository
import com.mariomora.utilia.domain.usecase.auth.*
import com.mariomora.utilia.domain.usecase.utilia.*

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
object ServiceLocator {
    lateinit var appContext: Context
        private set

    lateinit var storage: LocalStorage
        private set

    lateinit var dispatchers: DispatcherProvider
        private set

    lateinit var api: ApiEndpoints
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var utilIaRepository: UtilIaRepository
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        storage = SharedPreferencesStorage(appContext)
        dispatchers = DefaultDispatcherProvider()
        api = NetworkClient.createService(storage)
        authRepository = AuthRepositoryImpl(api, storage, dispatchers)
        utilIaRepository = UtilIaRepositoryImpl(api, dispatchers)
    }

    fun provideLoginWithEmailUseCase() = LoginWithEmailUseCase(authRepository)
    fun provideRegisterUseCase() = RegisterUseCase(authRepository)
    fun provideLoginWithGoogleUseCase() = LoginWithGoogleUseCase(authRepository)
    fun provideLoginAsGuestDemoUseCase() = LoginAsGuestDemoUseCase(authRepository)
    fun provideGetCurrentUserUseCase() = GetCurrentUserUseCase(authRepository)
    fun provideLogoutUseCase() = LogoutUseCase(authRepository)

    fun provideCalculateFiniquitoUseCase() = CalculateFiniquitoUseCase(utilIaRepository)
    fun provideAnalyzeContractUseCase() = AnalyzeContractUseCase(utilIaRepository)
    fun provideDraftOfficialDocumentUseCase() = DraftOfficialDocumentUseCase(utilIaRepository)
}
