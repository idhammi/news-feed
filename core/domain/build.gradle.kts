plugins {
    alias(libs.plugins.newsfeed.android.library)
}

android {
    namespace = "id.idham.newsfeed.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    // Koin
    implementation(libs.koin.core)
}
