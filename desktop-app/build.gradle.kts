import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose") version "1.11.1" apply false
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

// The project already uses "compose" for its version catalog, so the packaging
// extension must use a distinct name while retaining Compose Desktop's tasks.
val composeDesktopPackaging = extensions.create<DesktopExtension>("composeDesktopPackaging")
val releaseVersionName = providers.gradleProperty("releaseVersionName").orElse("1.0.0").get()

composeDesktopPackaging.application {
    mainClass = "chimahon.desktop.MainKt"

    nativeDistributions {
        targetFormats(TargetFormat.Exe)
        packageName = "Chimahon"
        packageVersion = releaseVersionName
        includeAllModules = true
        windows {
            exePackageVersion = releaseVersionName
        }
    }
}

afterEvaluate {
    Class.forName("org.jetbrains.compose.desktop.application.internal.ConfigureDesktopKt")
        .getMethod("configureDesktop", Project::class.java, DesktopExtension::class.java)
        .invoke(null, project, composeDesktopPackaging)
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
