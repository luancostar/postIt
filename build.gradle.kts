plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false // Padronizado
    alias(libs.plugins.google.ksp) apply false                   // Padronizado

    // ✅ LINHA FALTANTE ADICIONADA AQUI
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}