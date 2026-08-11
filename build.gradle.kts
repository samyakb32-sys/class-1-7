buildscript {
    repositories { google(); mavenCentral() }
    dependencies {
        // Applied conditionally by :app — see app/build.gradle.kts. Declaring the
        // classpath here is inert until something actually applies the plugin.
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
