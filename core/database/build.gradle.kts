import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("mihon.library")
    kotlin("multiplatform")
}

kotlin {
    androidTarget()
    jvm("desktop")
    mingwX64("windows")
    linuxX64("linux")
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.sqldelight.runtime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
                api(libs.okio)
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.bundles.test)
                runtimeOnly(libs.junit.platform.launcher)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

android {
    namespace = "tachiyomi.core.database"
}
