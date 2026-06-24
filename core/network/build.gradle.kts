import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.newsfeed.android.library)
    alias(libs.plugins.ksp)
}

// Read API key from local.properties
val localProperties = Properties().apply {
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

val apiKey: String = localProperties.getProperty("API_KEY", "")

android {
    namespace = "id.idham.newsfeed.core.network"

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://newsapi.org/\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.model)

    // Koin
    implementation(libs.koin.core)

    // Network
    api(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Logging
    implementation(libs.okhttp.logging)
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)
}
