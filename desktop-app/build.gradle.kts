import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    application
}

dependencies {
    implementation(projects.sharedUi)
    implementation("org.jetbrains.compose.desktop:desktop-jvm-${composeDesktopTarget()}:${compose.versions.multiplatform.get()}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

application {
    applicationName = "chimahon-desktop-smoke"
    mainClass.set("chimahon.desktop.MainKt")
}

fun composeDesktopTarget(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()

    return when {
        os.contains("windows") -> "windows-x64"
        os.contains("mac") && arch.contains("aarch64") -> "macos-arm64"
        os.contains("mac") -> "macos-x64"
        else -> "linux-x64"
    }
}
