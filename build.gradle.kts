import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "org.spreadme"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)

    // Module dependencies
    implementation(project(":mupdf"))

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.2")
    implementation("io.insert-koin", "koin-core", "3.1.6")

    implementation("com.itextpdf", "itext7-core", "7.2.1")

    implementation("org.xerial", "sqlite-jdbc", "3.36.0.3")
    implementation("org.jetbrains.exposed", "exposed-core", "0.38.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.38.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.38.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.38.1")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.github.microutils", "kotlin-logging-jvm", "2.1.21")

    testImplementation(kotlin("test-junit"))
    testImplementation("io.insert-koin", "koin-test", "3.1.6")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.io.path.ExperimentalPathApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    exclude("META-INF/BC1024KE.RSA", "META-INF/BC1024KE.SF", "META-INF/BC1024KE.DSA")
    exclude("META-INF/BC2048KE.RSA", "META-INF/BC2048KE.SF", "META-INF/BC2048KE.DSA")
}

tasks.jar {
    exclude("META-INF/BC1024KE.RSA", "META-INF/BC1024KE.SF", "META-INF/BC1024KE.DSA")
    exclude("META-INF/BC2048KE.RSA", "META-INF/BC2048KE.SF", "META-INF/BC2048KE.DSA")
}

compose.desktop {
    application {
        mainClass = "org.spreadme.pdfgadgets.AppKt"
        jvmArgs += listOf(
            "-Xms500m",
            "-Xmx1g",
            "-XX:ReservedCodeCacheSize=512m",
            "-XX:+UseG1GC",
            "-Xverify:none",
            "-XX:SoftRefLRUPolicyMSPerMB=50",
            "-XX:CICompilerCount=2",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-XX:-OmitStackTraceInFastThrow"
        )
        nativeDistributions {
            packageName = "PDFGadgets"
            packageVersion = project.version as String
            description = "pdf tools"
            copyright = "© 2022 wangshuwei6@gmail.com. All rights reserved."
            vendor = "shuwill"
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            modules(
                "jdk.crypto.ec",
                "java.sql",
                "java.sql.rowset"
            )

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            val iconsRoot = project.file("src/main/resources/drawables")

            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
                upgradeUuid = "FAFE561D-B48B-4121-B689-21A0377E6E6D"
                menuGroup = packageName
                perUserInstall = true
            }

            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
            }
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
}