plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.meuapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.meuapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ⬇️ 2. ADICIONE ESTAS DUAS LINHAS PARA O FIREBASE
    // O BOM (Bill of Materials) gerencia as versões das bibliotecas do Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Dependência para o Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")
}