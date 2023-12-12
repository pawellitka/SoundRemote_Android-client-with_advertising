plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.kapt")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.fake.soundremote"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.fake.soundremote"
        minSdk = 21
        targetSdk = 34
        versionCode = 2
        versionName = "0.3.0"
        testInstrumentationRunner = "com.fake.soundremote.CustomTestRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    buildToolsVersion = "34.0.0"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.aar", "*.jar"), "dir" to "libs")))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media)
    implementation(libs.androidx.ktx)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)  // For the predictive back gesture
    implementation(libs.bundles.androidx.lifeycle)
// Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    // UI
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsize)
    // Android Studio Preview support
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // UI Tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
// Instrumented tests
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.navigation.testing)
// Local tests
    testImplementation(libs.bundles.local.tests)
// Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
// Preference datastore
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.datastore.preferences)
// Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
// Navigation
    implementation(libs.androidx.navigation.compose)
// Accompanist
    implementation(libs.accompanist.permissions)
// Guava
    implementation(libs.guava)
}
