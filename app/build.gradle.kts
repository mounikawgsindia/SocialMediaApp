plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Required for Hilt
    id("com.google.dagger.hilt.android") // ✅ correct
    kotlin("kapt")
}

android {
    namespace = "com.wingspan.aimediahub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wingspan.aimediahub"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            val keystorePath = project.findProperty("KEYSTORE_FILE") as? String ?: "aimedia.jks"
            val storePassword = project.findProperty("KEYSTORE_PASSWORD") as? String ?: ""
            val keyAlias = project.findProperty("KEY_ALIAS") as? String ?: ""
            val keyPassword = project.findProperty("KEY_PASSWORD") as? String ?: ""
            storeFile = file(keystorePath)
            this.storePassword = storePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }
    buildTypes {
        debug {


            buildConfigField("boolean", "ENABLE_LOG", "true") // 👈 Enable logging
            buildConfigField("String", "BASE_URL", "\"${project.properties["BASE_URL"]}\"")
        }
        release {

            signingConfig= signingConfigs.getByName("release")
            buildConfigField("boolean", "ENABLE_LOG", "false") // 👈 Enable logging
            buildConfigField("String", "BASE_URL", "\"${project.properties["BASE_URL"]}\"")
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
    //coil
    implementation ("io.coil-kt:coil-compose:2.4.0")

    //twitter
    implementation(libs.scribejava.core.v833)
    implementation(libs.scribejava.apis.v833)
    implementation(libs.gson)
    //facebook
    implementation(libs.facebook.android.sdk)
    //retrofit
    implementation(libs.retrofit)
    implementation (libs.converter.gson)
    //coroutines
    implementation (libs.jetbrains.kotlinx.coroutines.android)

    //navigation
    implementation(libs.androidx.navigation.navigation.compose2)

    // Hilt
    implementation (libs.hilt.android)
    kapt (libs.hilt.android.compiler)

    //  Hilt + Jetpack
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose.v110alpha01)

    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}