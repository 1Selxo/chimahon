import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("mihon.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.ben-manes.versions")
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
                api(projects.core.extensions)
                api(project.dependencies.platform(kotlinx.coroutines.bom))
                api(kotlinx.coroutines.core)
                api(kotlinx.serialization.json)

                implementation(project.dependencies.platform(compose.bom))
                implementation(compose.runtime)
            }
        }
        val jvmCompatMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(projects.core.network)
                api(libs.injekt)
                api(libs.logcat)
                api(libs.rxjava)
                api(libs.jsoup)
                api(libs.okhttp.core)

                // SY -->
                api(projects.i18n)
                api(projects.i18nSy)
                api(kotlinx.reflect)
                // SY <--
            }
        }
        val androidMain by getting {
            dependsOn(jvmCompatMain)
            dependencies {
                implementation(projects.core.common)
                api(libs.preferencektx)

                // Workaround for https://youtrack.jetbrains.com/issue/KT-57605
                implementation(kotlinx.coroutines.android)
                implementation(project.dependencies.platform(kotlinx.coroutines.bom))
            }
        }
        val desktopMain by getting {
            dependsOn(jvmCompatMain)
        }
        val nativeMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:3.5.0")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.5.0")
            }
        }
        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:3.5.0")
            }
        }
        val windowsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:3.5.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
    namespace = "eu.kanade.tachiyomi.source"

    defaultConfig {
        consumerProguardFile("consumer-proguard.pro")
    }
}
