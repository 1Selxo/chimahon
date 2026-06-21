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
val releaseBuildNumber = providers.gradleProperty("releaseBuildNumber").orElse("1").get()
val windowsPackageVersion = releaseVersionName.toWindowsPackageVersion()

composeDesktopPackaging.application {
    mainClass = "chimahon.desktop.MainKt"

    nativeDistributions {
        targetFormats(*composeDesktopTargetFormats())
        packageName = "Chimahon"
        packageVersion = releaseVersionName
        description = "A native Kotlin Multiplatform manga reader for Chimahon."
        vendor = "Chimahon"
        includeAllModules = true
        macOS {
            bundleID = "app.chimahon.desktop"
            dockName = "Chimahon"
            packageBuildVersion = releaseBuildNumber
            dmgPackageVersion = releaseVersionName
        }
        windows {
            exePackageVersion = windowsPackageVersion
            msiPackageVersion = windowsPackageVersion
            menu = true
            menuGroup = "Chimahon"
            shortcut = true
            dirChooser = true
            upgradeUuid = "5e89900a-5c91-4ecb-8cc0-17d4d54a2c0e"
        }
        linux {
            packageName = "chimahon"
            appCategory = "Utility"
            menuGroup = "Utility"
            shortcut = true
            debMaintainer = "Chimahon"
            rpmLicenseType = "GPL-3.0"
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

fun composeDesktopTargetFormats(): Array<TargetFormat> {
    val os = System.getProperty("os.name").lowercase()

    return when {
        os.contains("windows") -> arrayOf(TargetFormat.Exe, TargetFormat.Msi)
        os.contains("mac") -> arrayOf(TargetFormat.Dmg)
        else -> arrayOf(TargetFormat.Deb, TargetFormat.Rpm)
    }
}

fun String.toWindowsPackageVersion(): String {
    val parts = trim()
        .removePrefix("v")
        .split('.', '-', '+')
        .mapNotNull { part -> part.takeWhile { character -> character.isDigit() }.toIntOrNull() }
        .take(4)
        .mapIndexed { index, part ->
            part.coerceIn(0, if (index < 2) 255 else 65_535)
        }
        .toMutableList()
    while (parts.size < 3) parts += 0
    return parts.joinToString(".")
}
