plugins {
    kotlin("jvm") version "2.1.21"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("com.electronwill.night-config:toml:3.8.3")
}
