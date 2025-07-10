// Define as versões das bibliotecas aqui no topo para fácil manutenção
val room_version = "2.6.1"
val lifecycle_version = "2.8.2"
val work_version = "2.9.0"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // ✅ ESTE PLUGIN ESTAVA FALTANDO
    id("com.google.devtools.ksp")
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // Alterado para 1.8, mais comum
        targetCompatibility = JavaVersion.VERSION_1_8 // Alterado para 1.8, mais comum
    }
    // Adicione esta seção se seu projeto usar Kotlin
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Dependências Padrão (removi o 'libs.' para simplificar)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dependência da Imagem Circular
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Dependências do Room, Lifecycle e WorkManager
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycle_version")
    implementation("androidx.work:work-runtime:$work_version")
}