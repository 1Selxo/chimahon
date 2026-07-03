import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File

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
val desktopJavaHome = resolveDesktopJavaHome()
val packageDescription = "Native desktop reader for manga, anime, novels, dictionaries, and immersion workflows."
val packageVendor = "Chimahon contributors"
val windowsPackageVersion = releaseVersionName.toWindowsPackageVersion()

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Chimahon Desktop",
            "Implementation-Version" to releaseVersionName,
            "Implementation-Vendor" to packageVendor,
        )
    }
}

composeDesktopPackaging.application {
    mainClass = "chimahon.desktop.MainKt"
    javaHome = desktopJavaHome
    jvmArgs(
        "-Dfile.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8",
        "-Dchimahon.desktop.version=$releaseVersionName",
    )

    buildTypes {
        release {
            proguard {
                isEnabled.set(false)
            }
        }
    }

    nativeDistributions {
        targetFormats(*composeDesktopTargetFormats())
        packageName = "Chimahon"
        packageVersion = releaseVersionName
        description = packageDescription
        vendor = packageVendor
        copyright = "Copyright (C) Chimahon contributors"
        licenseFile.set(rootProject.layout.projectDirectory.file("LICENSE"))
        includeAllModules = true
        macOS {
            packageName = "Chimahon"
            bundleID = "app.chimahon.desktop"
            dockName = "Chimahon"
            setDockNameSameAsPackageName = true
            appCategory = "public.app-category.education"
            packageBuildVersion = releaseBuildNumber
            dmgPackageVersion = releaseVersionName
            dmgPackageBuildVersion = releaseBuildNumber
        }
        windows {
            packageVersion = windowsPackageVersion
            exePackageVersion = windowsPackageVersion
            msiPackageVersion = windowsPackageVersion
            console = false
            menu = true
            menuGroup = "Chimahon"
            shortcut = true
            dirChooser = true
            perUserInstall = true
            upgradeUuid = "5e89900a-5c91-4ecb-8cc0-17d4d54a2c0e"
        }
        linux {
            packageName = "chimahon"
            appRelease = releaseBuildNumber
            appCategory = "Education"
            menuGroup = "Education"
            shortcut = true
            debMaintainer = packageVendor
            debPackageVersion = releaseVersionName
            rpmPackageVersion = releaseVersionName
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

fun resolveDesktopJavaHome(): String {
    val candidates = buildList {
        providers.gradleProperty("desktopJavaHome").orNull?.let(::add)
        providers.environmentVariable("JPACKAGE_JAVA_HOME").orNull?.let(::add)
        providers.environmentVariable("JAVA_HOME").orNull?.let(::add)
        providers.systemProperty("java.home").orNull?.let(::add)
        findJpackageJavaHomeOnPath()?.let(::add)
        addAll(commonJdkHomes())
    }
        .map { File(it).absoluteFile.normalize().path }
        .distinctBy { it.lowercase() }

    candidates.firstOrNull(::hasJpackageTool)?.let { return it }

    if (isDesktopPackagingTaskRequested()) {
        val checkedHomes = candidates.joinToString(separator = "\n  - ", prefix = "\n  - ")
        throw GradleException(
            "Desktop native packaging requires a JDK that includes jpackage.\n" +
                "Set -PdesktopJavaHome=... or JPACKAGE_JAVA_HOME to a full JDK 17+ path.\n" +
                "Checked:$checkedHomes",
        )
    }

    return candidates.firstOrNull() ?: System.getProperty("java.home")
}

fun hasJpackageTool(javaHome: String): Boolean {
    val executableName = if (System.getProperty("os.name").lowercase().contains("windows")) {
        "jpackage.exe"
    } else {
        "jpackage"
    }
    return File(javaHome, "bin/$executableName").isFile
}

fun findJpackageJavaHomeOnPath(): String? {
    val executableName = if (System.getProperty("os.name").lowercase().contains("windows")) {
        "jpackage.exe"
    } else {
        "jpackage"
    }
    return System.getenv("PATH")
        ?.split(File.pathSeparator)
        ?.asSequence()
        ?.map { File(it, executableName) }
        ?.firstOrNull { it.isFile }
        ?.parentFile
        ?.parentFile
        ?.absolutePath
}

fun commonJdkHomes(): List<String> {
    val home = System.getProperty("user.home")
    val workspace = rootProject.layout.projectDirectory.asFile.absolutePath
    val roots = buildList {
        add("$workspace/.gradle/codex-jdks")
        add("$workspace/.gradle/jdks")
        if (System.getProperty("os.name").lowercase().contains("windows")) {
            add("C:/Program Files/Java")
            add("C:/Program Files/Eclipse Adoptium")
            add("C:/Program Files/Microsoft")
            add("C:/Program Files/JetBrains")
            add("$home/.jdks")
            add("$home/.gradle/jdks")
        } else {
            add("/Library/Java/JavaVirtualMachines")
            add("/usr/lib/jvm")
            add("$home/.jdks")
            add("$home/.gradle/jdks")
        }
    }

    return roots
        .asSequence()
        .map(::File)
        .filter { it.isDirectory }
        .flatMap { root ->
            sequenceOf(root) + (root.listFiles()?.asSequence()?.filter { it.isDirectory } ?: emptySequence())
        }
        .map { directory ->
            when {
                directory.name == "Contents" -> directory.parentFile
                File(directory, "Contents/Home").isDirectory -> File(directory, "Contents/Home")
                else -> directory
            }
        }
        .map { it.absolutePath }
        .toList()
}

fun isDesktopPackagingTaskRequested(): Boolean {
    val packagingNames = listOf("package", "distributable", "runtimeimage", "checkruntime")
    return gradle.startParameter.taskNames.any { taskName ->
        val requestedName = taskName.substringAfterLast(':').lowercase()
        packagingNames.any(requestedName::contains)
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
