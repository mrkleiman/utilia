package com.mariomora.utilia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.mariomora.utilia.core.di.ServiceLocator
import com.mariomora.utilia.presentation.auth.AuthViewModel
import com.mariomora.utilia.presentation.auth.LoginScreen
import com.mariomora.utilia.presentation.home.UtilIaHomeScreen
import com.mariomora.utilia.presentation.home.UtilIaHomeViewModel
import com.mariomora.utilia.presentation.onboarding.OnboardingScreen
import com.mariomora.utilia.presentation.onboarding.OnboardingViewModel

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
@Composable
fun MainNavigation() {
    val isOnboardingDone = remember { ServiceLocator.storage.getBoolean("onboarding_done", false) }
    val isAuthenticated = remember { ServiceLocator.authRepository.getCurrentUser() != null }

    val start = when {
        !isOnboardingDone -> NavOnboarding
        !isAuthenticated -> NavLogin
        else -> NavHome
    }

    val backStack = rememberNavBackStack(start)

    val onboardingViewModel = remember { OnboardingViewModel(ServiceLocator.storage) }
    val authViewModel = remember {
        AuthViewModel(
            ServiceLocator.provideLoginWithEmailUseCase(),
            ServiceLocator.provideLoginWithGoogleUseCase(),
            ServiceLocator.provideRegisterUseCase(),
            ServiceLocator.provideLoginAsGuestDemoUseCase(),
            ServiceLocator.provideGetCurrentUserUseCase()
        )
    }
    val homeViewModel = remember {
        UtilIaHomeViewModel(
            ServiceLocator.provideGetCurrentUserUseCase(),
            ServiceLocator.provideLogoutUseCase(),
            ServiceLocator.provideCalculateFiniquitoUseCase(),
            ServiceLocator.provideAnalyzeContractUseCase(),
            ServiceLocator.provideDraftOfficialDocumentUseCase()
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<NavOnboarding> {
                OnboardingScreen(onboardingViewModel, onFinish = {
                    backStack.clear()
                    backStack.add(NavLogin)
                })
            }
            entry<NavLogin> {
                LoginScreen(authViewModel, onSuccess = {
                    backStack.clear()
                    backStack.add(NavHome)
                })
            }
            entry<NavHome> {
                UtilIaHomeScreen(
                    homeViewModel,
                    onLogout = {
                        backStack.clear()
                        backStack.add(NavLogin)
                    },
                    onShowTutorial = { backStack.add(NavOnboarding) }
                )
            }
        }
    )
}
