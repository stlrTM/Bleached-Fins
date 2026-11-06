import org.gradle.kotlin.dsl.register
import stellar.Dependency
import stellar.GenerateMetaTask
import stellar.ModEntry
import stellar.ModsToml

plugins {
    kotlin("jvm") version "2.1.21"
    id("idea")
    id("net.neoforged.moddev") version "2.0.141"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.2"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

fun Project.propOrRoot(key: String): String = (if (hasProperty(key)) property(key) else rootProject.property(key)) as String

version = propOrRoot("version")
group = "stellar"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://thedarkcolour.github.io/KotlinForForge/") }
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:5.9.0")
}

kotlin {
    jvmToolchain(21)
}

val metahsfghsthrtxhdfbvgzrfgfhsfhsfghfgjghjghdfghrgeth = ModsToml(
    modLoader = "kotlinforforge",
    loaderVersion = propOrRoot("loader_version_range"),
    license = propOrRoot("license"),
    mods = listOf(
        ModEntry(
            modId = propOrRoot("id"),
            version = propOrRoot("version"),
            displayName = propOrRoot("nmamnamnmamnamnmanmmanmamnmanmmnmnmamnm"),
            description = propOrRoot("description"),
            authors = propOrRoot("authors"),
        )
    ),
    dependencies = mapOf(
        propOrRoot("id") to listOf(
            Dependency(modId = "neoforge", versionRange = propOrRoot("neo_version_range")),
            Dependency(modId = "minecraft", versionRange = propOrRoot("mc_version_range")),
            Dependency(modId = "kotlinforforge", versionRange = propOrRoot("loader_version_range")),
        )
    ),
) // leftovers that im too lazy to refactor,might make a whole gradle plugin for this

val generateModMetadata = tasks.register<GenerateMetaTask>("generateModMetadata") {
    meta.set(metahsfghsthrtxhdfbvgzrfgfhsfhsfghfgjghjghdfghrgeth)
    val outputDirectory = layout.buildDirectory.dir("generated/sources/modMetadata")
    outputDir.set(outputDirectory)
}

sourceSets {
    main {
        resources {
            exclude("src/generated/resources/.cache")
            srcDir("src/main/resources")
            srcDir("src/generated/resources")
            srcDir(generateModMetadata)
        }
    }
}

tasks.processResources {
    dependsOn(generateModMetadata)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // typoi kotlin opyat kopiruet moi resursy!!!
}

neoForge {

    version = project.property("neo_version") as String

    parchment {
        mappingsVersion = property("parchment_build")!! as String
        minecraftVersion = property("parchment_version")!! as String
    }

    mods {
        create("aquatic") {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {

        create("client") {
            client()
        }

        create("server") {
            server()
        }

        create("data") {
            data()
            programArguments.addAll("--all")
            programArguments.addAll(
                "--mod", "ether_aquatic",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }


        configureEach {
            systemProperty("mixin.debug.export", "true")
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
            gameDirectory.set(file("run/${name}"))

            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            jvmArgument("-Xmx8g")

            systemProperty("log4j.skipJansi", "false")
            systemProperty("terminal.ansi", "true")
            systemProperty("log4j2.configurationFile", rootProject.file("log4j2.xml").absolutePath)
        }
    }
}