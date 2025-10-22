val kotlin_version: String by project
val logback_version: String by project
val ktor_version = "3.3.1"

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("io.ktor.plugin") version "3.3.1"
}

group = "space.sadcat"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Ktor server ---
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")

    // --- JSON support ---
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // --- Config & logging ---
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:3.3.1")
    implementation("io.ktor:ktor-server-host-common:3.3.1")
    implementation("io.ktor:ktor-server-status-pages:3.3.1")
    implementation("io.ktor:ktor-server-core:3.3.1")

    // --- Tests ---
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

kotlin {
    jvmToolchain(21)
}
