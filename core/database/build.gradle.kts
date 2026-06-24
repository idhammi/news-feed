plugins {
    alias(libs.plugins.newsfeed.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "id.idham.newsfeed.core.database"
}

dependencies {
    api(projects.core.model)

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Koin
    implementation(libs.koin.core)
}
