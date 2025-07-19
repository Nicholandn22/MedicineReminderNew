//import jdk.tools.jlink.internal.Platform.runtime

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Untuk Room
    id("kotlin-parcelize")
    id("com.google.gms.google-services") // ✅ Tambahkan ini
}

android {
    namespace = "com.example.medicineremindernew"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.medicineremindernew"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ✅ Firebase BOM untuk sinkronisasi versi
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // ✅ Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // ✅ Firebase Authentication (opsional)
    implementation("com.google.firebase:firebase-auth")

    // ✅ Firebase Analytics (opsional)
    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-firestore-ktx")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")


    // Room dependencies (masih dipertahankan jika masih digunakan)
    val room_version = "2.6.1"
//    implementation("androidx.room:room-runtime:$room_version")
//    kapt("androidx.room:room-compiler:$room_version")
//    implementation("androidx.room:room-ktx:$room_version")

    // AndroidX & Compose dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.biometric:biometric:1.1.0")
}
