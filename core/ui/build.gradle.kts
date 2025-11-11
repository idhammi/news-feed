plugins {
    alias(libs.plugins.newsfeed.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "id.idham.newsfeed.core.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
}
