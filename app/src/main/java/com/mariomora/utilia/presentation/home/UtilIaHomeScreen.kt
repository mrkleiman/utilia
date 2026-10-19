package com.mariomora.utilia.presentation.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mariomora.utilia.presentation.components.MexicanFlagBadge
import com.mariomora.utilia.presentation.components.PrimaryButton
import com.mariomora.utilia.presentation.components.StatCard
import com.mariomora.utilia.theme.MexicoGreen
import com.mariomora.utilia.theme.MexicoRed
import com.mariomora.utilia.theme.WarningAmber
import java.util.Locale

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilIaHomeScreen(
    viewModel: UtilIaHomeViewModel,
    onLogout: () -> Unit,
    onShowTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("UtilIA Asesor Legal", fontWeight = FontWeight.Bold)
                        Text(
                            text = state.currentUser?.displayName ?: "Ciudadano",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShowTutorial) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial")
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("Calculadora LFT") },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Explicador IA") },
                    icon = { Icon(Icons.Default.FindInPage, contentDescription = null) }
                )
                Tab(
                    selected = state.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("Redactor Oficios") },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) }
                )
            }

            when (state.selectedTab) {
                0 -> FiniquitoCalculatorTab(viewModel, state)
                1 -> ContractAnalyzerTab(viewModel, state)
                2 -> OfficialDocumentTab(viewModel, state)
            }
        }
    }
}

@Composable
fun FiniquitoCalculatorTab(viewModel: UtilIaHomeViewModel, state: UtilIaUiState) {
    var salaryInput by remember { mutableStateOf("350.00") }
    var daysWorkedInput by remember { mutableStateOf("280") }
    var seniorityInput by remember { mutableStateOf("2.5") }
    var isDismissal by remember { mutableStateOf(true) }
    var pendingVacations by remember { mutableStateOf("4") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MexicanFlagBadge("Ley Federal del Trabajo (Reforma Vacaciones Dignas)")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Parámetros del Trabajador", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = salaryInput,
                    onValueChange = { salaryInput = it },
                    label = { Text("Salario Diario ($ MXN)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysWorkedInput,
                        onValueChange = { daysWorkedInput = it },
                        label = { Text("Días trabajados este año") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = seniorityInput,
                        onValueChange = { seniorityInput = it },
                        label = { Text("Años de antigüedad") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                OutlinedTextField(
                    value = pendingVacations,
                    onValueChange = { pendingVacations = it },
                    label = { Text("Días de vacaciones pendientes") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿Fue Despido Injustificado?", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = isDismissal,
                        onCheckedChange = { isDismissal = it }
                    )
                }

                PrimaryButton(
                    text = "Calcular Liquidación / Finiquito",
                    onClick = {
                        val sal = salaryInput.toDoubleOrNull() ?: 0.0
                        val days = daysWorkedInput.toIntOrNull() ?: 180
                        val sen = seniorityInput.toDoubleOrNull() ?: 1.0
                        val vac = pendingVacations.toIntOrNull() ?: 0
                        viewModel.calculateFiniquito(sal, days, sen, isDismissal, vac)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading
                )
            }
        }

        state.finiquitoResult?.let { res ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Resultado del Cálculo Legal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Neto a Pagar",
                            value = "$${String.format(Locale.US, "%,.2f", res.netoPagar)}",
                            subtitle = "Monto final transferible",
                            icon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MexicoGreen) },
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Subtotal Finiquito",
                            value = "$${String.format(Locale.US, "%,.2f", res.subtotalFiniquito)}",
                            subtitle = "Derechos devengados",
                            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider()

                    Text("Desglose Conforme a la LFT:", fontWeight = FontWeight.Bold)
                    Text("• Aguinaldo Proporcional (Art. 87): $${String.format(Locale.US, "%,.2f", res.aguinaldoProporcional)}")
                    Text("• Vacaciones Proporcionales (Art. 76): $${String.format(Locale.US, "%,.2f", res.vacacionesProporcionales)}")
                    Text("• Prima Vacacional 25% (Art. 80): $${String.format(Locale.US, "%,.2f", res.primaVacacional)}")
                    if (res.indemnizacion90Dias > 0) {
                        Text("• Indemnización Constitucional 90 días (Art. 48): $${String.format(Locale.US, "%,.2f", res.indemnizacion90Dias)}", color = MexicoGreen, fontWeight = FontWeight.Bold)
                    }
                    if (res.primaAntiguedad > 0) {
                        Text("• Prima de Antigüedad (Art. 162): $${String.format(Locale.US, "%,.2f", res.primaAntiguedad)}")
                    }
                    Text("• Retención ISR Estimada: -$${String.format(Locale.US, "%,.2f", res.isrRetencionEstimada)}", color = MexicoRed)

                    Text(
                        text = res.legalBasis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ContractAnalyzerTab(viewModel: UtilIaHomeViewModel, state: UtilIaUiState) {
    var contractText by remember {
        mutableStateOf(
            "El Prestador se obliga a cumplir un horario de lunes a viernes de 8:00 a 18:00 hrs bajo órdenes directas de la Dirección. Asimismo, se prohíbe prestar servicios a cualquier otra empresa durante los próximos 5 años tras la terminación, con una penalización del 200% de los honorarios percibidos."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MexicanFlagBadge("Protección Legal y Detección de Cláusulas Abusivas")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Pega el texto de tu contrato laboral o de servicios:", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = contractText,
                    onValueChange = { contractText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Copia y pega las cláusulas aquí...") }
                )

                PrimaryButton(
                    text = "Analizar Contrato con IA",
                    onClick = { viewModel.analyzeContract(contractText) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading
                )
            }
        }

        state.contractAnalysis?.let { analysis ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Resumen en Lenguaje Claro", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(analysis.summaryInPlainSpanish)

                    if (analysis.redFlags.isNotEmpty()) {
                        Text("Alertas y Cláusulas Riesgosas:", fontWeight = FontWeight.Bold, color = WarningAmber)
                        analysis.redFlags.forEach { flag ->
                            Text("⚠️ $flag", color = WarningAmber, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (analysis.workerRightsObserved.isNotEmpty()) {
                        Text("Derechos Protegidos:", fontWeight = FontWeight.Bold, color = MexicoGreen)
                        analysis.workerRightsObserved.forEach { right ->
                            Text("✅ $right", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Text("Recomendación:", fontWeight = FontWeight.Bold)
                    Text(analysis.recommendation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun OfficialDocumentTab(viewModel: UtilIaHomeViewModel, state: UtilIaUiState) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("Solicitud de aclaración y pago de finiquito") }
    var recipient by remember { mutableStateOf("Dirección de Recursos Humanos / Empresa S.A. de C.V.") }
    var subject by remember { mutableStateOf("Aclaración formal de finiquito de conformidad con la LFT") }
    var details by remember {
        mutableStateOf(
            "El día 15 de octubre de 2026 concluí mi relación laboral habiendo laborado 2.5 años. A la fecha no se me ha hecho entrega del desglose de prestaciones irrenunciables ni del pago correspondiente."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MexicanFlagBadge("Fundamento: Artículo 8° Constitucional (Derecho de Petición)")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Generador de Oficios y Peticiones", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Destinatario (Autoridad / Empresa)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Asunto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Detalles de los hechos / solicitud") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )

                PrimaryButton(
                    text = "Generar Escrito Legal",
                    onClick = { viewModel.draftDocument(title, recipient, subject, details) },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = state.isLoading
                )
            }
        }

        state.officialDraft?.let { draft ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Borrador Listo", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Oficio UtilIA", draft.bodyText)
                                clipboard.setPrimaryClip(clip)
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar oficio")
                        }
                    }

                    Text(draft.bodyText, style = MaterialTheme.typography.bodySmall)

                    Text(
                        text = "Fundamentos: " + draft.legalArticlesReferenced.joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
