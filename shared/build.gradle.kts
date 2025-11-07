import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            // Koin Android
            implementation(libs.koin.android)

            // Firebase
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.common)
            implementation(libs.kotlinx.coroutines.play.services)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.cucumber.java)
                implementation(libs.cucumber.junit)
                implementation(libs.spek.dsl)
                implementation(libs.spek.runner.junit5)
            }
        }
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // DateTime
            implementation(libs.kotlinx.datetime)

            // Ktor client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // Koin for dependency injection
            api(libs.koin.core)

            // room
            implementation(libs.room.runtime)
            implementation(libs.room.compiler)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.property)
            implementation(libs.mockk)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "ke.kiura.cashi.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
