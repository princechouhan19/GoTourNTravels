buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.mappls.com/repository/mappls/") }
    }
    dependencies {
        classpath("com.mappls.services:mappls-services:1.0.1")
    }
}

// Top-level project build file
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}
