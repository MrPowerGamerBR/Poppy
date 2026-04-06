import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "com.mrpowergamerbr.poppy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "com.mrpowergamerbr.poppy.PoppyKt"
}

tasks.test {
    useJUnitPlatform()
}