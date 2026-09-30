plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// The file a person copies onto a USB stick, so give it a name they can recognise.
base { archivesName.set("MathsAdventureTV") }

android {
    namespace = "com.gumthala.learningapp.tv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gumthala.learningapp.tv"
        // Android TV 5.0+ (Compose's own floor). Plenty of cheap TV boxes still ship Android 7-9.
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)

    testImplementation(libs.junit)
    // android.jar's org.json is a stub that throws in JVM unit tests; use the real one there.
    testImplementation(libs.org.json)
}
