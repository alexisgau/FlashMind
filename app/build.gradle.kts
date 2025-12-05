import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrainsKotlinSerialization)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id ("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")

}




android {
    namespace = "com.example.flashmind"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.flashmind"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Cargar propiedades desde local.properties
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                load(FileInputStream(localPropertiesFile))
            }
        }

        // Obtener la clave API - IMPORTANTE: sin comillas adicionales
        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY", "")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    aaptOptions {
        noCompress.add("txt")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.pdf.viewer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation("androidx.compose.material3:material3:1.2.1")


    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")

    implementation("com.google.firebase:firebase-storage")

    // Manejo de credenciales (Authentication API)
    implementation ("androidx.credentials:credentials:1.3.0-alpha05")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0-alpha05")

// Google Identity
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.datastore:datastore-preferences:1.1.0")

    implementation(libs.generativeai)
//    implementation("com.google.firebase:firebase-ai")
    //workManager
    implementation("androidx.work:work-runtime-ktx:2.10.2")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    //Upload file libraries
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.apache.poi:poi-scratchpad:5.2.5")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") 
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // O la versión que uses
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")




}