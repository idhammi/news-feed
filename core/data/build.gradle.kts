plugins {
    alias(libs.plugins.newsfeed.android.library)
}

android {
    namespace = "id.idham.newsfeed.core.data"
}

dependencies {
    api(projects.core.network)
    api(projects.core.database)
    api(libs.androidx.paging.runtime)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Koin
    implementation(libs.koin.core)

    // Coroutines
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}