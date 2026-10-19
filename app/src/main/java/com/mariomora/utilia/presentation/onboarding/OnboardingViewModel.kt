package com.mariomora.utilia.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.mariomora.utilia.core.storage.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class OnboardingViewModel(private val storage: LocalStorage) : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    fun nextPage(total: Int, onComplete: () -> Unit) {
        if (_currentPage.value < total - 1) {
            _currentPage.value += 1
        } else {
            storage.putBoolean("onboarding_done", true)
            onComplete()
        }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        storage.putBoolean("onboarding_done", true)
        onComplete()
    }
}
