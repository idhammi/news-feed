plugins {
    alias(libs.plugins.newsfeed.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "id.idham.newsfeed.core.model"
}

dependencies {
    api(libs.kotlinx.serialization.json)
}
