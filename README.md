# ⚖️ UtilIA - Asesor Legal y Laboral para México

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blueviolet.svg)](https://developer.android.com/jetpack/compose)
[![LFT](https://img.shields.io/badge/Legislación-LFT%20%2F%20Art.%208%C2%B0%20Const.-red.svg)]()
[![License](https://img.shields.io/badge/Author-Mario%20Mora%20--%20Android%20Dev-emerald.svg)]()

**UtilIA** es una aplicación nativa de Android que empodera legalmente a trabajadores y ciudadanos en México. Proporciona una calculadora laboral de liquidaciones y finiquitos de alta precisión basada en la Ley Federal del Trabajo, un analizador de contratos con inteligencia artificial para detectar subordinación encubierta y un redactor de oficios y peticiones formales con fundamento en el **Artículo 8° de la Constitución Mexicana**.

---

## 🚀 Características Principales (3 Pestañas de Herramientas)

### 1. 🧮 Calculadora Laboral LFT (Finiquitos y Liquidaciones)
Calcula con exactitud matemática y jurídica el monto que le corresponde al trabajador:
- **Aguinaldo Proporcional:** Conforme al **Artículo 87 LFT** (mínimo 15 días anuales).
- **Vacaciones Dignas Proporcionales:** Conforme al **Artículo 76 LFT** (12 días desde el 1er año, incrementando progresivamente).
- **Prima Vacacional:** 25% de ley conforme al **Artículo 80 LFT**.
- **Indemnización Constitucional de 90 Días:** Aplicable por ley en caso de despido injustificado conforme al **Artículo 48 LFT**.
- **Prima de Antigüedad:** 12 días de salario por año laborado, topado a 2 salarios mínimos conforme al **Artículo 162 LFT**.
- **Estimación de Retención de ISR:** Desglose entre parte exenta y base gravable sujeta a retención fiscal.

### 2. 🔍 Explicador Inteligente de Contratos
- Traduce cláusulas complejas a español sencillo.
- **Detector de Subordinación Simulada:** Identifica cuando un contrato de honorarios civiles exige horario forzoso o dependencia jerárquica (relación laboral encubierta sujeta a cuotas IMSS).
- **Alertas de Cláusulas Abusivas:** Detección de penalizaciones desproporcionadas y restricciones inconstitucionales de no competencia (Art. 5° Constitucional).

### 3. 📝 Redactor de Oficios y Peticiones Formales
- Genera escritos formales listos para imprimir o enviar con fecha automatizada y redacción solemne.
- **Fundamento Constitucional:** Basado en el **Artículo 8° de la Constitución Política de los Estados Unidos Mexicanos (Derecho de Petición)** y la Ley Federal de Protección al Consumidor (LFPC).
- **Copia con un Clic:** Botón directo para copiar el escrito completo al portapapeles del dispositivo.

---

## 🏛️ Arquitectura & Principios SOLID

- **Clean Architecture Modular:**
  - `core`: Inyección de dependencias (`ServiceLocator`), `DispatcherProvider`, almacenamiento y red.
  - `domain`: Modelos inmutables (`FiniquitoInput`, `FiniquitoResult`, `ContractAnalysisResult`, `OfficialDocumentDraft`), interfaces de repositorio y casos de uso con SRP.
  - `data`: Repositorios con Retrofit 2, OkHttp y cálculo local de alta precisión.
  - `presentation`: Jetpack Compose, Material Design 3, ViewModels reactivos (`StateFlow`) y Navigation 3.
- **UDF (Unidirectional Data Flow):** Separación total de responsabilidades y flujo unidireccional de estados.
- **Principios SOLID:**
  - **S:** Casos de uso atómicos (`CalculateFiniquitoUseCase`, `AnalyzeContractUseCase`, `DraftOfficialDocumentUseCase`).
  - **O:** Estructura modular abierta a nuevas leyes o reformas fiscales.
  - **L:** Sustitución limpia de fuentes de datos para pruebas automatizadas.
  - **I:** Interfaces segregadas y enfocadas.
  - **D:** ViewModels y UseCases dependen de abstracciones.

---

## 🇲🇽 Leyes de México Respetadas

- **Constitución Política de los Estados Unidos Mexicanos:**
  - Artículo 5° (Libertad de trabajo).
  - Artículo 8° (Derecho de petición pacífica y respetuosa).
  - Artículo 123 (Bases del derecho laboral mexicano).
- **Ley Federal del Trabajo (LFT):** Artículos 48, 76, 80, 87 y 162.
- **Políticas de Google Play Store:** Mínimo privilegio (solo permisos de red `INTERNET` y `ACCESS_NETWORK_STATE`).

---

## 🧪 Pruebas Unitarias

Ejecuta las pruebas unitarias que validan el cálculo del finiquito voluntario vs. liquidación por despido injustificado y análisis de cláusulas:

```bash
./gradlew testDebugUnitTest
```

---

## 📦 Compilación

Genera el APK debug independiente:

```bash
./gradlew assembleDebug
```

---

## 👤 Autor

**Mario Mora - Senior Android Engineer & Software Architect**  
*Firma en código: "Mario Mora - Android Dev"*
