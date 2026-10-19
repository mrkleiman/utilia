package com.mariomora.utilia.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mariomora.utilia.presentation.components.MexicanFlagBadge
import com.mariomora.utilia.presentation.components.PrimaryButton

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */

data class OnboardingStep(
    val title: String,
    val description: String,
    val legalNote: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit
) {
    val page by viewModel.currentPage.collectAsState()

    val pages = listOf(
        OnboardingStep(
            title = "Calculadora Laboral LFT",
            description = "Calcula con precisión exacta tu finiquito o liquidación con las reformas de Vacaciones Dignas (Art. 76), Aguinaldo (Art. 87) e Indemnización Constitucional.",
            legalNote = "Cumple con las leyes de la República Mexicana.",
            icon = Icons.Default.Calculate
        ),
        OnboardingStep(
            title = "Explicador de Contratos con IA",
            description = "Traduce contratos complejos a español claro y detecta cláusulas abusivas como horarios encubiertos o penalizaciones ilegales.",
            legalNote = "Protege tus derechos laborales y comerciales.",
            icon = Icons.Default.FindInPage
        ),
        OnboardingStep(
            title = "Redactor de Oficios y Peticiones",
            description = "Genera escritos formales fundamentados en el Artículo 8° de la Constitución Mexicana (Derecho de Petición) listos para imprimir o enviar.",
            legalNote = "Válido ante cualquier autoridad o dependencia.",
            icon = Icons.Default.Description
        )
    )

    val current = pages[page]

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MexicanFlagBadge("UtilIA Legal")
                TextButton(onClick = { viewModel.completeOnboarding(onFinish) }) {
                    Text("Omitir")
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = current.icon,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = current.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = current.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = current.legalNote,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == page) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == page) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                PrimaryButton(
                    text = if (page == pages.lastIndex) "Comenzar Ahora" else "Siguiente",
                    onClick = { viewModel.nextPage(pages.size, onFinish) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
