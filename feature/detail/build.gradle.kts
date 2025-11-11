plugins {
    alias(libs.plugins.newsfeed.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "id.idham.newsfeed.feature.detail"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
